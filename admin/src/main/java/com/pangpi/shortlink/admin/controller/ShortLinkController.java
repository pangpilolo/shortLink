package com.pangpi.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.pangpi.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.pangpi.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.pangpi.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkBaseInfoRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.pangpi.shortlink.admin.remote.ShortLinkRemoteService;
import com.pangpi.shortlink.admin.utils.EasyExcelWebUtil;
import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.result.Results;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/short-link/admin")
public class ShortLinkController {

    private final ShortLinkRemoteService shortLinkService;

    @PostMapping("/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return shortLinkService.createShortLink(requestParam);
    }

    @PostMapping("/v1/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> res = shortLinkService.batchCreateShortLink(requestParam);
        if (res.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = res.getData().getBaseLinkInfos();
            EasyExcelWebUtil.writer(response, "批量创建短链接-SaaS短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }

    @GetMapping("/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkService.pageShortLink(requestParam);
    }

    /**
     * 修改短链接
     */
    @PostMapping("/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

}
