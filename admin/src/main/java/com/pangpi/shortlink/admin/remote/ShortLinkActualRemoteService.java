package com.pangpi.shortlink.admin.remote;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.admin.remote.dto.req.*;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.pangpi.shortlink.convention.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接远程调用中台服务
 */
@FeignClient("short-link-project")
public interface ShortLinkActualRemoteService {

    /**
     * 新增短链接RPC
     * @param requestParam 新增请求参数
     * @return 响应结果
     */
    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接RPC
     * @param requestParam 分页查询请求参数
     * @return 分页结果
     */
    @GetMapping("/api/short-link/v1/page")
    Result<IPage<ShortLinkPageRespDTO>> pageShortLink(@RequestParam("requestParam") ShortLinkPageReqDTO requestParam);

    /**
     * 分页查询短链接RPC
     * @param requestParam 分页查询请求参数
     * @return 分页结果
     */
    @GetMapping("/api/short-link/v1/count")
    Result<List<ShortLinkGroupCountRespDTO>> listShortLinkGroupCount(@RequestParam("requestParam") List<String> requestParam);

    /**
     * 修改短链接
     * @param requestParam 修改参数
     */
    @PutMapping("/api/short-link/v1/update")
    void updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);

    /**
     * 根据url获取对应网站的title
     * @param url 网站url
     * @return title
     */
    @GetMapping("/api/short-link/v1/title")
    Result<String> getTitleByUrl(@RequestParam("url") String url);

    /**
     * 短链接保存至回收站
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/recycleBin/save")
    void saveToRecycleBin(@RequestBody RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站的短链接
     * @return 分页后的回收站短链接
     */
    @PostMapping("/api/short-link/v1/recycle-bin/page")
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(@RequestBody ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 回收站恢复短链接
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    void recover(@RequestBody RecycleBinRecoverReqDTO requestParam);

    /**
     * 删除短链接
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/remove")
    void removeShortLink(@RequestBody RecycleBinRemoveReqDTO requestParam);

    /**
     * 批量创建短链接
     * @param requestParam 批量新增参数
     * @return 批量新增后的响应结果
     */
    @PostMapping("/api/short-link/v1/create/batch")
    Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam);
}
