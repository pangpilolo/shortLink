package com.pangpi.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.result.Results;
import com.pangpi.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.pangpi.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.pangpi.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.pangpi.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-link")
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;

    /**
     * 将短链接保存至
     */
    @PostMapping("/v1/recycle-bin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.saveToRecycleBin(requestParam);
        return Results.success();
    }


    /**
     * 分页查询回收站短链接
     */
    @PostMapping("/v1/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(@RequestBody ShortLinkRecycleBinPageReqDTO requestParam) {
        return Results.success(recycleBinService.pageShortLink(requestParam));
    }


    /**
     *  短链接恢复
     */
    @PostMapping("/v1/recycle-bin/recover")
    public Result<Void> recover(@RequestBody RecycleBinRecoverReqDTO requestParam) {
        recycleBinService.recover(requestParam);
        return Results.success();
    }


    /**
     *  短链接恢复
     */
    @DeleteMapping("/v1/recycle-bin/remove")
    public Result<Void> removeShortLink(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        recycleBinService.removeShortLink(requestParam);
        return Results.success();
    }





}
