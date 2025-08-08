package com.pangpi.shortlink.admin.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.admin.common.biz.user.UserContext;
import com.pangpi.shortlink.admin.common.constant.WebConstants;
import com.pangpi.shortlink.admin.dao.entity.GroupDO;
import com.pangpi.shortlink.admin.remote.ShortLinkActualRemoteService;
import com.pangpi.shortlink.admin.remote.dto.req.*;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.pangpi.shortlink.admin.service.GroupService;
import com.pangpi.shortlink.admin.remote.ShortLinkRemoteService;
import com.pangpi.shortlink.convention.exception.ServiceException;
import com.pangpi.shortlink.convention.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 短链接远程调用中台服务
 */
@Service
@RequiredArgsConstructor
public class ShortLinkRemoteServiceImpl implements ShortLinkRemoteService {

    private final GroupService groupService;

    private final ShortLinkActualRemoteService actualRemoteService;

    public Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {
        return actualRemoteService.createShortLink(requestParam);
    }


     public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return actualRemoteService.pageShortLink(requestParam);
    }


    public Result<List<ShortLinkGroupCountRespDTO>> listShortLinkGroupCount(List<String> requestParam) {
        return actualRemoteService.listShortLinkGroupCount(requestParam);
    }

    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        actualRemoteService.updateShortLink(requestParam);
    }

    @Override
    public Result<String> getTitleByUrl(String url) {
        return actualRemoteService.getTitleByUrl(url);
    }

    @Override
    public void saveToRecycleBin(RecycleBinSaveReqDTO requestParam) {
        actualRemoteService.saveToRecycleBin(requestParam);
    }

    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        // 查询用户名下的分组
        String username = UserContext.getUsername();
        LambdaQueryWrapper<GroupDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupDO::getUsername, username)
                .eq(GroupDO::getDelFlag, WebConstants.NO_DELETE_FLAG);
        List<String> gidList = groupService.list(queryWrapper).stream().map(GroupDO::getGid).collect(Collectors.toList());
        if (CollUtil.isEmpty(gidList)) {
            throw new ServiceException("无当前用户的分组信息");
        }
        requestParam.setGidList(gidList);
        return actualRemoteService.pageRecycleBinShortLink(requestParam);
    }

    @Override
    public void recover(RecycleBinRecoverReqDTO requestParam) {
        actualRemoteService.recover(requestParam);
    }

    @Override
    public void removeShortLink(RecycleBinRemoveReqDTO requestParam) {
        actualRemoteService.removeShortLink(requestParam);
    }

    @Override
    public Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        return actualRemoteService.batchCreateShortLink(requestParam);
    }
}
