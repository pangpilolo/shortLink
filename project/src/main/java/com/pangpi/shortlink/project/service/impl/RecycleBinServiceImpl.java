package com.pangpi.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pangpi.shortlink.convention.exception.ClientException;
import com.pangpi.shortlink.convention.exception.ServiceException;
import com.pangpi.shortlink.convention.util.BeanCopyUtils;
import com.pangpi.shortlink.project.common.constant.ServiceConstant;
import com.pangpi.shortlink.project.dao.entity.ShortLinkDO;
import com.pangpi.shortlink.project.dao.mapper.ShortLinkMapper;
import com.pangpi.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.pangpi.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.pangpi.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.pangpi.shortlink.project.service.RecycleBinService;
import com.pangpi.shortlink.project.util.LinkUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.pangpi.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINK_GOTO_KEY_FORMAT;
import static com.pangpi.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINT_GOTO_IS_NULL_KEY_FORMAT;

/**
 * 短链接回收站管理接口
 */
@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements RecycleBinService {

    private final StringRedisTemplate stringRedisTemplate;

    private final RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter;

    @Override
    public void saveToRecycleBin(RecycleBinSaveReqDTO requestParam) {
        // 判断是否在布隆过滤器
        if (!shortLinkCreateCachePenetrationBloomFilter.contains(requestParam.getFullShortUrl())) {
            throw new ClientException("短链接错误");
        }

        // 加入回收站主要是修改是否启用状态
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_OPEN)
                .eq(ShortLinkDO::getDelFlag,ServiceConstant.NO_DELETE_FLAG)
                .set(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_CLOSE);
        if (!update(updateWrapper)) {
            throw new ServiceException("短链接回收失败");
        }
        // 删除短链接缓存
        stringRedisTemplate.delete(String.format(SHORT_LINK_GOTO_KEY_FORMAT, requestParam.getFullShortUrl()));
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ShortLinkDO::getGid, requestParam.getGidList())
                .eq(ShortLinkDO::getDelFlag, ServiceConstant.NO_DELETE_FLAG)
                // 回收站就是分页查询禁用状态的短链接
                .eq(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_CLOSE)
                .orderByDesc(ShortLinkDO::getCreateTime);
        IPage<ShortLinkDO> resPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resPage.convert(item -> {
            ShortLinkPageRespDTO res = BeanCopyUtils.copyBean(item, ShortLinkPageRespDTO.class);
            res.setDomain("http://" + item.getDomain());
            return res;
        });
    }

    @Override
    public void recover(RecycleBinRecoverReqDTO requestParam) {
        // 判断是否在布隆过滤器
        if (!shortLinkCreateCachePenetrationBloomFilter.contains(requestParam.getFullShortUrl())) {
            throw new ClientException("短链接错误");
        }
        // 恢复也是修改启用状态
        LambdaUpdateWrapper<ShortLinkDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_CLOSE)
                .eq(ShortLinkDO::getDelFlag,ServiceConstant.NO_DELETE_FLAG)
                .set(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_OPEN);
        if (!update(updateWrapper)) {
            throw new ServiceException("短链接恢复失败");
        }
        // 删除短链接isNULL缓存
        stringRedisTemplate.delete(String.format(SHORT_LINT_GOTO_IS_NULL_KEY_FORMAT, requestParam.getFullShortUrl()));
    }

    @Override
    public void removeShortLink(RecycleBinRemoveReqDTO requestParam) {
        // 判断是否在布隆过滤器
        if (!shortLinkCreateCachePenetrationBloomFilter.contains(requestParam.getFullShortUrl())) {
            throw new ClientException("短链接错误");
        }
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getEnableStatus, ServiceConstant.ENABLE_STATUS_CLOSE)
                .eq(ShortLinkDO::getDelFlag,ServiceConstant.NO_DELETE_FLAG);
        remove(queryWrapper);
    }
}
