package com.pangpi.shortlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.pangpi.shortlink.admin.remote.dto.req.RecycleBinRemoveReqDTO;
import com.pangpi.shortlink.admin.remote.dto.req.RecycleBinSaveReqDTO;
import com.pangpi.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.pangpi.shortlink.admin.remote.ShortLinkRemoteService;
import com.pangpi.shortlink.convention.result.Result;
import com.pangpi.shortlink.convention.result.Results;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-link/admin")
@RequiredArgsConstructor
public class RecycleBinController {

    private final ShortLinkRemoteService shortLinkService;

    /**
     * 将短链接保存至
     */
    @PostMapping("/recycleBin/save")
    public Result<Void> saveRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam) {
        shortLinkService.saveToRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/recycle-bin/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        return shortLinkService.pageRecycleBinShortLink(requestParam);
    }

    /**
     *  短链接恢复
     */
    @GetMapping("/recycle-bin/recover")
    public Result<Void> recover(RecycleBinRecoverReqDTO requestParam) {
        shortLinkService.recover(requestParam);
        return Results.success();
    }

    /**
     *  短链接恢复
     */
    @DeleteMapping("/v1/recycle-bin/remove")
    public Result<Void> removeShortLink(@RequestBody RecycleBinRemoveReqDTO requestParam) {
        shortLinkService.removeShortLink(requestParam);
        return Results.success();
    }


}
