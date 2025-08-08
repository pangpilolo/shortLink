package com.pangpi.shortlink.project.mq.comsumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pangpi.shortlink.convention.exception.ServiceException;
import com.pangpi.shortlink.project.common.constant.RedisKeyConstant;
import com.pangpi.shortlink.project.common.constant.ServiceConstant;
import com.pangpi.shortlink.project.dao.entity.*;
import com.pangpi.shortlink.project.dao.mapper.*;
import com.pangpi.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.pangpi.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.pangpi.shortlink.project.service.LinkAccessLogsService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShortLinkStatsSaveConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final LinkGotoMapper linkGotoMapper;

    private final LinkAccessStatsMapper linkAccessStatsMapper;

    private final LinkAccessLogsService linkAccessLogsService;

    private final LinkLocaleStatsMapper linkLocaleStatsMapper;

    private final LinkDeviceStatsMapper linkDeviceStatsMapper;

    private final LinkNetworkStatsMapper linkNetworkStatsMapper;

    private final LinkOsStatsMapper linkOsStatsMapper;

    private final LinkBrowserStatsMapper linkBrowserStatsMapper;


    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        // 获取stream
        String stream = message.getStream();
        RecordId messageId = message.getId();
        if (messageQueueIdempotentHandler.isMessageBeingConsumed(messageId.toString())) {
            // 如果存在缓存，则查看是否成功消费
            if (messageQueueIdempotentHandler.isAccomplish(messageId.toString())) {
                return;
            }
            // 待考证，如果没有成功消费为什么不去消费？
            // 因为消息队列部署到多个机器上，可能是此条消息正在被消费
            throw new ServiceException("消息未完成流程，需要消息队列重试");
        }
        try {
            // 解析消息为监控信息对象
            Map<String, String> value = message.getValue();
            ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(value.get("statsRecord"), ShortLinkStatsRecordDTO.class);
            // 消费消息
            actualSaveShortLinkStats(statsRecord);
            // 消费完成删除消息队列中的消息
            stringRedisTemplate.opsForStream().delete(Objects.requireNonNull(stream), messageId.getValue());
        } catch (Throwable e) {
            // 消息消费出现问题，设置幂等标志
            messageQueueIdempotentHandler.delMessageProcessed(messageId.toString());
            log.error("记录短链接监控消费异常", e);
            throw e;
        }
        // 正常消费成功会走到这里,去设置消息消费成功
        messageQueueIdempotentHandler.setAccomplish(messageId.toString());
    }


    @Transactional(rollbackFor = RuntimeException.class)
    public void actualSaveShortLinkStats(ShortLinkStatsRecordDTO statsRecord) {
        String fullShortUrl = statsRecord.getFullShortUrl();
        RLock lock = redissonClient.getLock(String.format(RedisKeyConstant.LOCK_GID_UPDATE_KEY, fullShortUrl));
        lock.lock();
        try {
            // 从goto表获取gid
            Date now = new Date();
            LambdaQueryWrapper<LinkGotoDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LinkGotoDO::getFullShortUrl, fullShortUrl);
            LinkGotoDO gotoDO = linkGotoMapper.selectOne(queryWrapper);
            if (ObjUtil.isEmpty(gotoDO)) {
                log.error("当前短链接:{},在路由表中未找到对应gid", fullShortUrl);
                throw new ServiceException("当前短链接在路由表中未找到对应gid");
            }
            String gid = gotoDO.getGid();

            // 访问区间和访问量统计
            int hour = DateUtil.hour(now, true);
            int week = DateUtil.weekOfMonth(now);
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .pv(1)
                    .uv(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .uip(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .weekday(week)
                    .hour(hour)
                    .date(now)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);

            // 访问地区统计
            Map<String, Object> localParamMap = new HashMap<>();
            localParamMap.put("ip", statsRecord.getRemoteAddr());
            localParamMap.put("key", statsLocaleAmapKey);
            String localeResultStr = HttpUtil.get(ServiceConstant.AMAP_REMOTE_URL, localParamMap);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                // 获取省
                String province = localeResultObj.getString("province");
                String city = localeResultObj.getString("city");
                String adCode = localeResultObj.getString("adcode");
                boolean hasValue = StrUtil.isNotBlank(province);
                LinkLocaleStatsDO localeStatsDO = LinkLocaleStatsDO
                        .builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(now)
                        .province(hasValue ? province : "未知")
                        .city(hasValue ? city : "未知")
                        .adcode(hasValue ? adCode : "未知")
                        .country("中国")
                        .cnt(1)
                        .build();
                localeStatsDO.setUpdateTime(now);
                localeStatsDO.setCreateTime(now);
                localeStatsDO.setDelFlag(ServiceConstant.NO_DELETE_FLAG);
                linkLocaleStatsMapper.shortLinkLocaleStats(localeStatsDO);

                // 操作系统统计
                LinkOsStatsDO osStatsDO = LinkOsStatsDO.builder()
                        .os(statsRecord.getOs())
                        .gid(gid)
                        .fullShortUrl(fullShortUrl)
                        .date(now)
                        .cnt(1)
                        .build();
                osStatsDO.setCreateTime(now);
                osStatsDO.setUpdateTime(now);
                osStatsDO.setDelFlag(ServiceConstant.NO_DELETE_FLAG);
                linkOsStatsMapper.shortLinkOSStates(osStatsDO);

                // 浏览器统计
                LinkBrowserStatsDO browserStatsDO = LinkBrowserStatsDO.builder()
                        .browser(statsRecord.getBrowser())
                        .cnt(1)
                        .gid(gid)
                        .fullShortUrl(fullShortUrl)
                        .date(new Date())
                        .build();
                browserStatsDO.setCreateTime(now);
                browserStatsDO.setUpdateTime(now);
                browserStatsDO.setDelFlag(ServiceConstant.NO_DELETE_FLAG);
                linkBrowserStatsMapper.shortLinkBrowserStates(browserStatsDO);

                // 访问设备统计
                LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                        .device(statsRecord.getDevice())
                        .cnt(1)
                        .gid(gid)
                        .fullShortUrl(fullShortUrl)
                        .date(now)
                        .build();
                linkDeviceStatsDO.setCreateTime(now);
                linkDeviceStatsDO.setUpdateTime(now);
                linkDeviceStatsDO.setDelFlag(ServiceConstant.NO_DELETE_FLAG);
                linkDeviceStatsMapper.shortLinkDeviceStats(linkDeviceStatsDO);

                // 访问网络统计
                LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                        .network(statsRecord.getNetwork())
                        .cnt(1)
                        .gid(gid)
                        .fullShortUrl(fullShortUrl)
                        .date(now)
                        .build();
                linkNetworkStatsDO.setCreateTime(now);
                linkNetworkStatsDO.setUpdateTime(now);
                linkNetworkStatsDO.setDelFlag(ServiceConstant.NO_DELETE_FLAG);
                linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);

                // 日志统计
                LinkAccessLogsDO accessLogsDO = LinkAccessLogsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .os(statsRecord.getOs())
                        .browser(statsRecord.getBrowser())
                        .ip(statsRecord.getRemoteAddr())
                        .user(statsRecord.getUv())
                        .build();
                accessLogsDO.setUpdateTime(now);
                accessLogsDO.setCreateTime(now);
                accessLogsDO.setDelFlag(ServiceConstant.NO_DELETE_FLAG);
                linkAccessLogsService.save(accessLogsDO);
            }
        } catch (Throwable ex) {
            log.error("短链接监控统计出现错误", ex);
        } finally {
            lock.unlock();
        }
    }

}
