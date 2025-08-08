package com.pangpi.shortlink.project.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.result.Results;
import com.pangpi.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.pangpi.shortlink.project.handler.CustomBlockHandler;
import com.pangpi.shortlink.project.service.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ShortLinkController {


    private final ShortLinkService shortLinkService;

    @GetMapping("/{short-url}")
    public void restoreUrl(@PathVariable("short-url") String shortUrl, HttpServletRequest request, HttpServletResponse response) {
        shortLinkService.restoreUrl(shortUrl, request, response);
    }

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        ShortLinkCreateRespDTO res = shortLinkService.createShortLink(requestParam);
        return Results.success(res);
    }

    /**
     * 批量创建短链接
     */
    @PostMapping("/api/short-link/v1/create/batch")
    public Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam) {
        return Results.success(shortLinkService.batchCreateShortLink(requestParam));
    }

    /**
     * 修改短链接
     */
    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 分页查询短链接集合
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        IPage<ShortLinkPageRespDTO> res = shortLinkService.pageShortLink(requestParam);
        return Results.success(res);
    }

    /**
     * 统计分组下的短链接个数
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountRespDTO>> listShortLinkGroupCount(@RequestParam List<String> requestParam) {
        List<ShortLinkGroupCountRespDTO> res = shortLinkService.listShortLinkGroupCount(requestParam);
        return Results.success(res);
    }



}
