package com.pangpi.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pangpi.shortlink.convention.exception.ClientException;
import com.pangpi.shortlink.convention.exception.ServiceException;
import com.pangpi.shortlink.convention.util.BeanCopyUtils;
import com.pangpi.shortlink.project.common.constant.RedisKeyConstant;
import com.pangpi.shortlink.project.common.constant.ServiceConstant;
import com.pangpi.shortlink.project.common.enums.ShortLinkErrorCodeEnum;
import com.pangpi.shortlink.project.common.enums.ValidDateTypeEnum;
import com.pangpi.shortlink.project.config.GotoDomainWhiteListConfiguration;
import com.pangpi.shortlink.project.dao.entity.*;
import com.pangpi.shortlink.project.dao.mapper.*;
import com.pangpi.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.pangpi.shortlink.project.dto.resp.*;
import com.pangpi.shortlink.project.mq.producer.DelayShortLinkStatsProducer;
import com.pangpi.shortlink.project.mq.producer.ShortLinkStatsSaveProducer;
import com.pangpi.shortlink.project.service.LinkAccessLogsService;
import com.pangpi.shortlink.project.service.LinkGotoService;
import com.pangpi.shortlink.project.service.ShortLinkService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pangpi.shortlink.project.util.HashUtil;
import com.pangpi.shortlink.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Op;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.pangpi.shortlink.project.common.constant.RedisKeyConstant.*;


/**
 * (Link)表服务实现类
 *
 * @author pangpi
 * @since 2024-06-28 16:21:41
 */
@Slf4j
@RequiredArgsConstructor
@Service("shortlinkService")
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter;

    private final LinkGotoService linkGotoService;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final GotoDomainWhiteListConfiguration gotoDomainWhiteListConfiguration;

    private final ShortLinkStatsSaveProducer shortLinkStatsSaveProducer;

    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;

    @Value("${short-link.domain.default}")
    private String shortLinkDefaultDomain;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        verificationWhitelist(requestParam.getOriginUrl());
        if (StrUtil.isBlank(requestParam.getOriginUrl())) {
            throw new ServiceException(ShortLinkErrorCodeEnum.ORIGIN_URL_NOT_BLANK);
        }
        if (StrUtil.isBlank(requestParam.getDomain())) {
            throw new ServiceException(ShortLinkErrorCodeEnum.DOMAIN_NOT_BLANK);
        }
        String shortLinkUri = generateShortLinkUri(shortLinkDefaultDomain, requestParam.getOriginUrl());
        ShortLinkDO saveDO = BeanCopyUtils.copyBean(requestParam, ShortLinkDO.class);
        saveDO.setFullShortUrl(shortLinkDefaultDomain + "/" + shortLinkUri);
        saveDO.setShortUri(shortLinkUri);
        // 设置默认启用状态为 启用→1
        saveDO.setEnableStatus(ServiceConstant.ENABLE_STATUS_OPEN);
        // 如果有效期类型，创建类型为空则默认值
        saveDO.setCreatedType(ObjUtil.isEmpty(saveDO.getCreatedType()) ? 0 : saveDO.getCreatedType());
        saveDO.setValidDateType(ObjUtil.isEmpty(saveDO.getValidDateType()) ? 0 : saveDO.getValidDateType());
        saveDO.setFavicon(getFavicon(requestParam.getOriginUrl()));
        LinkGotoDO linkGotoDO = new LinkGotoDO();
        linkGotoDO.setFullShortUrl(saveDO.getFullShortUrl());
        linkGotoDO.setGid(saveDO.getGid());
        try {
            save(saveDO);
            // 将路由入库
            linkGotoService.save(linkGotoDO);
        } catch (DuplicateKeyException e) {
            log.error("短链接:{},重复入库", saveDO.getFullShortUrl());
            throw new ServiceException(ShortLinkErrorCodeEnum.GENERATE_SO_MUCH);
        }
        shortLinkCreateCachePenetrationBloomFilter.add(saveDO.getFullShortUrl());
        // 新增是进行cache预热
        stringRedisTemplate.opsForValue().set(
                String.format(SHORT_LINK_GOTO_KEY_FORMAT, saveDO.getFullShortUrl()),
                saveDO.getOriginUrl(),
                LinkUtil.getLinkCacheValidTime(saveDO.getValidDate()),
                TimeUnit.MILLISECONDS
        );
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + saveDO.getFullShortUrl())
                .group(new ShortLinkGroupRespDTO(saveDO.getGid(), null))
                .domain(shortLinkDefaultDomain)
                .originUrl(saveDO.getOriginUrl())
                .build();
    }

    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        ShortLinkCreateReqDTO save = BeanUtil.toBean(requestParam, ShortLinkCreateReqDTO.class);
        List<ShortLinkBaseInfoRespDTO> result = new ArrayList<>();
        int finishTotal = 0;
        for (int i = 0; i < originUrls.size(); i++) {
            save.setOriginUrl(originUrls.get(i));
            save.setDescribe(describes.get(i));
            try {
                ShortLinkCreateRespDTO shortLinkResp = createShortLink(save);
                ShortLinkBaseInfoRespDTO baseInfoRespDTO = ShortLinkBaseInfoRespDTO.builder()
                        .describe(save.getDescribe())
                        .fullShortUrl(shortLinkResp.getFullShortUrl())
                        .originUrl(save.getOriginUrl())
                        .build();
                result.add(baseInfoRespDTO);
                finishTotal++;
            } catch (Throwable ex) {
                log.error("批量创建短链接失败，原始参数：{}", originUrls.get(i));
            }
        }
        return ShortLinkBatchCreateRespDTO.builder()
                .total(finishTotal)
                .baseLinkInfos(result)
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, ServiceConstant.NO_DELETE_FLAG)
                .eq(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_OPEN)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resPage.convert(item -> {
            ShortLinkPageRespDTO res = BeanCopyUtils.copyBean(item, ShortLinkPageRespDTO.class);
            res.setDomain("http://" + item.getDomain());
            return res;
        });
    }

    @Override
    public List<ShortLinkGroupCountRespDTO> listShortLinkGroupCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("gid,count(*) as shortLinkCount")
                .eq("enable_status", ServiceConstant.ENABLE_STATUS_OPEN)
                .in("gid", requestParam)
                .groupBy("gid");
        List<Map<String, Object>> res = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(res, ShortLinkGroupCountRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        verificationWhitelist(requestParam.getOriginUrl());
        // 根据原本的gid查询短链接
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, ServiceConstant.NO_DELETE_FLAG)
                .eq(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_OPEN);
        ShortLinkDO queryLink = getOne(queryWrapper);
        if (ObjUtil.isEmpty(queryLink)) {
            throw new ClientException("短链接记录不存在");
        }
        // 根据是否改变分组来决定修改
        if (ObjUtil.equals(requestParam.getGid(), requestParam.getOriginGid())) {
            // 没有修改分组，则直接修改即可
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getDelFlag, ServiceConstant.NO_DELETE_FLAG)
                    .eq(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_OPEN)
                    .set(StringUtil.isNotBlank(requestParam.getOriginUrl()), ShortLinkDO::getOriginUrl, requestParam.getOriginUrl())
                    .set(ObjUtil.isNotEmpty(requestParam.getCreatedType()), ShortLinkDO::getCreatedType, requestParam.getCreatedType())
                    .set(ShortLinkDO::getValidDateType, requestParam.getValidDateType())
                    .set(ShortLinkDO::getDescribe, requestParam.getDescribe());
            if (ValidDateTypeEnum.PERMANENT.getType().equals(requestParam.getValidDateType())) {
                updateWrapper.set(ShortLinkDO::getValidDate, null);
            } else {
                updateWrapper.set(ShortLinkDO::getValidDate, requestParam.getValidDate());
            }
            update(updateWrapper);
        } else {
            // 通过上锁保证统计信息能正常统计到对应的表中
            RLock lock = redissonClient.getLock(String.format(RedisKeyConstant.LOCK_GID_UPDATE_KEY, requestParam.getFullShortUrl()));
            lock.lock();
            try {
                // 如果修改了分组先把原本的短链接删除
                remove(queryWrapper);
                ShortLinkDO save = ShortLinkDO.builder()
                        .domain(queryLink.getDomain())
                        .shortUri(queryLink.getShortUri())
                        .fullShortUrl(queryLink.getFullShortUrl())
                        .originUrl(requestParam.getOriginUrl())
                        .gid(requestParam.getGid())
                        .createdType(requestParam.getCreatedType())
                        .validDateType(requestParam.getValidDateType())
                        .validDate(requestParam.getValidDate())
                        .describe(requestParam.getDescribe())
                        .enableStatus(ServiceConstant.ENABLE_STATUS_OPEN)
                        .build();
                save.setDelFlag(ServiceConstant.NO_DELETE_FLAG);
                save(save);

                // 同时修改路由表,这里因为路由表分表了，所以需要删除再新增
                LambdaUpdateWrapper<LinkGotoDO> gotoQueryWrapper = new LambdaUpdateWrapper<>();
                gotoQueryWrapper.eq(LinkGotoDO::getFullShortUrl, queryLink.getFullShortUrl())
                        .set(LinkGotoDO::getGid, requestParam.getGid());
                linkGotoService.remove(gotoQueryWrapper);

                LambdaQueryWrapper<LinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                        .eq(LinkGotoDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(LinkGotoDO::getGid, queryLink.getGid());
                LinkGotoDO shortLinkGotoDO = linkGotoService.getOne(linkGotoQueryWrapper);
                linkGotoService.remove(linkGotoQueryWrapper);
                shortLinkGotoDO.setGid(requestParam.getGid());
                linkGotoService.save(shortLinkGotoDO);
            } finally {
                lock.unlock();
            }
            // 如果修改了过期类型和过期时间就删除goto缓存，避免缓存不失效
            if (!Objects.equals(queryLink.getValidDate(), requestParam.getValidDate())
            || !Objects.equals(queryLink.getValidDateType(), requestParam.getValidDateType())
                    || !Objects.equals(queryLink.getOriginUrl(), requestParam.getOriginUrl())) {
                stringRedisTemplate.delete(String.format(SHORT_LINK_GOTO_KEY_FORMAT, queryLink.getFullShortUrl()));
                // 因为如果修改了过期时间可能就存在原本在库中的短链接是过期的，被放置了isNull的缓存
                // 所以如果现在永久有效 或者 当前设置过期的时间还没有过期那么就删除 isNull缓存
                Date currentDate = new Date();
                if (queryLink.getValidDate() != null && queryLink.getValidDate().before(currentDate)) {
                    if (Objects.equals(requestParam.getValidDateType(), ValidDateTypeEnum.PERMANENT.getType()) || requestParam.getValidDate().after(currentDate)) {
                        stringRedisTemplate.delete(String.format(SHORT_LINT_GOTO_IS_NULL_KEY_FORMAT, requestParam.getFullShortUrl()));
                    }
                }
            }
        }
    }

    private void verificationWhitelist(String originUrl) {
        Boolean enable = gotoDomainWhiteListConfiguration.getEnable();
        if (enable == null || !enable) {
            return;
        }
        String domain = LinkUtil.extractDomain(originUrl);
        if (StrUtil.isBlank(domain)) {
            throw new ServiceException("短链接生成错误");
        }
        if (!gotoDomainWhiteListConfiguration.getDetails().contains(domain)) {
            throw new ClientException("演示环境为避免恶意攻击，请生成以下网站跳转链接：" + gotoDomainWhiteListConfiguration.getNames());
        }
    }

    @Override
    public void restoreUrl(String shortUrl, HttpServletRequest request, HttpServletResponse response) {
        // 根据配置的域名去根据端口号修改逻辑
        String portStr = Optional.of(request.getServerPort())
                .filter(item -> !Objects.equals(item, 80))
                .map(String::valueOf)
                .map(item -> ":" + item)
                .orElse("");
        // 获取域名，组合完整短链接
        String fullShortUrl = request.getServerName() + portStr + "/" + shortUrl;
        // 判断是否在布隆过滤器中，因为如果布隆过滤器中说没有那么就是一定没有
        if (!shortLinkCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
            responseSendRedirectToNotFound(response);
            return;
        }
        String originalUrl = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINK_GOTO_KEY_FORMAT, fullShortUrl));
        if (StrUtil.isNotBlank(originalUrl)) {
            responseSendRedirectToOriginal(response, request, originalUrl, fullShortUrl);
            return;
        }
        // 特殊情况，穿布隆过滤器的情况
        String isNullUrl = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINT_GOTO_IS_NULL_KEY_FORMAT, fullShortUrl));
        if (StrUtil.isNotBlank(isNullUrl)) {
            responseSendRedirectToNotFound(response);
            return;
        }
        // 如果缓存中拿不到，则双重检查锁进行查库
        RLock lock = redissonClient.getLock(String.format(SHORT_LINK_GOTO_LOCK_KEY_FORMAT, fullShortUrl));
        lock.lock();
        try {
            originalUrl = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINK_GOTO_KEY_FORMAT, fullShortUrl));
            if (StrUtil.isNotBlank(originalUrl)) {
                responseSendRedirectToOriginal(response, request, originalUrl, fullShortUrl);
                return;
            }
            // 特殊情况，穿布隆过滤器的情况
            isNullUrl = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINT_GOTO_IS_NULL_KEY_FORMAT, fullShortUrl));
            if (StrUtil.isNotBlank(isNullUrl)) {
                // 重定向到不存在页面
                responseSendRedirectToNotFound(response);
                return;
            }
            // 路由表获取短链接的gid
            LambdaQueryWrapper<LinkGotoDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(LinkGotoDO::getFullShortUrl, fullShortUrl);
            LinkGotoDO gotoDO = linkGotoService.getOne(queryWrapper);
            // 如果不存在直接返回异常，说明布隆过滤器误判了
            if (ObjUtil.isEmpty(gotoDO)) {
                // 将空值放入redis中避免穿透,设置3分钟的过期时间
                stringRedisTemplate.opsForValue().set(String.format(SHORT_LINT_GOTO_IS_NULL_KEY_FORMAT, fullShortUrl), "-", 3, TimeUnit.MINUTES);
                responseSendRedirectToNotFound(response);
                // 选择直接return是因为懒得浪费性能去处理异常了
                return;
            }
            // 根据gid获取对应的原始链接
            LambdaQueryWrapper<ShortLinkDO> linkQueryWrapper = new LambdaQueryWrapper<>();
            linkQueryWrapper.eq(ShortLinkDO::getGid, gotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, gotoDO.getFullShortUrl())
                    .eq(ShortLinkDO::getDelFlag, ServiceConstant.NO_DELETE_FLAG)
                    .eq(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_OPEN);
            ShortLinkDO shortLinkDO = getOne(linkQueryWrapper);
            if (ObjUtil.isNotEmpty(shortLinkDO)) {
                // 检查有效期是否过期
                if (ObjUtil.isNotEmpty(shortLinkDO.getValidDate()) && shortLinkDO.getValidDate().before(new Date())) {
                    // 如果这是一个已经过期的短链接，则添加为空缓存
                    stringRedisTemplate.opsForValue().set(String.format(SHORT_LINT_GOTO_IS_NULL_KEY_FORMAT, fullShortUrl), "-", 3, TimeUnit.MINUTES);
                    responseSendRedirectToNotFound(response);
                    return;
                }
                originalUrl = shortLinkDO.getOriginUrl();
                //                stringRedisTemplate.opsForValue().set(String.format(SHORT_LINK_GOTO_KEY_FORMAT, fullShortUrl), originalUrl);
                // 根据有效时间进行缓存
                stringRedisTemplate.opsForValue().set(
                        String.format(SHORT_LINK_GOTO_KEY_FORMAT, shortLinkDO.getFullShortUrl()),
                        shortLinkDO.getOriginUrl(),
                        LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()),
                        TimeUnit.MILLISECONDS
                );
                responseSendRedirectToOriginal(response, request, originalUrl, fullShortUrl);
            } else {
                // 这个短链接可能在短链接表中删除，但是路由表没有更新，数据异常导致的
                stringRedisTemplate.opsForValue().set(String.format(SHORT_LINT_GOTO_IS_NULL_KEY_FORMAT, fullShortUrl), "-", 3, TimeUnit.MINUTES);
                responseSendRedirectToNotFound(response);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shortLinkStats(ShortLinkStatsRecordDTO statsRecord) {
        Map<String, String> producerMap = new HashMap<>();
        producerMap.put("statsRecord", JSON.toJSONString(statsRecord));
        // 消息队列为什么选用RocketMQ？详情查看：https://nageoffer.com/shortlink/question
        shortLinkStatsSaveProducer.sendMessage(producerMap);
    }


    private void responseSendRedirect(HttpServletResponse response, String originUrl) {
        try {
            response.sendRedirect(originUrl);
        } catch (Exception e) {
            log.error("链接跳转出现异常,原始链接:{}", originUrl);
        }
    }

    /**
     * 重定向到不存在页面
     */
    private void responseSendRedirectToNotFound(HttpServletResponse response) {
        responseSendRedirect(response, "/page/notfound");
    }

    /**
     * 重定向到指定地址，并统计信息
     */
    private void responseSendRedirectToOriginal(HttpServletResponse response, HttpServletRequest request,
                                                String originUrl, String fullShortUrl) {
        // 统计
        shortLinkStats(buildLinkStatsRecordAndSetUser(fullShortUrl, request, response));
        responseSendRedirect(response, originUrl);
    }

    /**
     * 根据原始链接获取其网站图标
     *
     * @param url 原始链接
     * @return 图标地址
     */
    @SneakyThrows
    public String getFavicon(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (ObjUtil.isNotEmpty(faviconLink)) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }

    private String generateShortLinkUri(String domain, String shortLinkUri) {
        int customGenerateCount = 0;
        String generateUri;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException(ShortLinkErrorCodeEnum.GENERATE_SO_MUCH);
            }
            generateUri = HashUtil.hashToBase62(shortLinkUri + UUID.randomUUID());
            if (!shortLinkCreateCachePenetrationBloomFilter.contains(domain + "/" + generateUri)) {
                return generateUri;
            }
            customGenerateCount++;
        }
    }


    private ShortLinkStatsRecordDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, HttpServletRequest request, HttpServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean(false);
        Cookie[] cookies = request.getCookies();
        // 判断cookie
        AtomicReference<String> res = new AtomicReference<>();
        Runnable addResponseCookieTask = () -> {
            // 没有uv或者cookie不存在时执行的任务
            String uvId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("uv", uvId);
            res.set(uvId);
            cookie.setMaxAge(60 * 60 * 24 * 30);
            // 当前cookie设置为当访问这个短链接时才携带
            cookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            response.addCookie(cookie);
            uvFirstFlag.set(true);
            stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uvId);
        };
        // 判断cookie中是否有uv，如果有则说明已经访问过了，所以只需要增加pv即可
        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies).filter(item -> Objects.equals(item.getName(), "uv")).findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(item -> {
                        res.set(item);
                        // 如果有uv这个cookie时，
                        Long add = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, item);
                        uvFirstFlag.set(add != null && add > 0);
                    }, addResponseCookieTask);
        } else {
            addResponseCookieTask.run();
        }
        String actualIp = LinkUtil.getIp(request);
        String os = LinkUtil.getOs(request);
        String browser = LinkUtil.getBrowser(request);
        String device = LinkUtil.getDevice(request);
        String network = LinkUtil.getNetwork(request);
        String user = res.get();
        Long uipAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UIP_KEY + fullShortUrl, actualIp);
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
        return ShortLinkStatsRecordDTO.builder()
                .fullShortUrl(fullShortUrl)
                .uv(user)
                .uvFirstFlag(uvFirstFlag.get())
                .uipFirstFlag(uipFirstFlag)
                .remoteAddr(actualIp)
                .os(os)
                .browser(browser)
                .device(device)
                .network(network)
                .currentDate(new Date())
                .build();
    }
}
