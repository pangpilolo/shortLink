package com.pangpi.shortlink.admin.remote;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.admin.remote.dto.req.*;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import com.pangpi.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.pangpi.shortlink.convention.result.Result;

import java.util.List;

/**
 * 短链接远程调用中台服务
 */
public interface ShortLinkRemoteService {

    /**
     * 新增短链接RPC
     * @param requestParam 新增请求参数
     * @return 响应结果
     */
    Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接RPC
     * @param requestParam 分页查询请求参数
     * @return 分页结果
     */
    Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 分页查询短链接RPC
     * @param requestParam 分页查询请求参数
     * @return 分页结果
     */
    Result<List<ShortLinkGroupCountRespDTO>> listShortLinkGroupCount(List<String> requestParam);

    /**
     * 修改短链接
     * @param requestParam 修改参数
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 根据url获取对应网站的title
     * @param url 网站url
     * @return title
     */
    Result<String> getTitleByUrl(String url);

    /**
     * 短链接保存至回收站
     * @param requestParam 请求参数
     */
    void saveToRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站的短链接
     * @return 分页后的回收站短链接
     */
    Result<IPage<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 回收站恢复短链接
     * @param requestParam 请求参数
     */
    void recover(RecycleBinRecoverReqDTO requestParam);

    /**
     * 删除短链接
     * @param requestParam 请求参数
     */
    void removeShortLink(RecycleBinRemoveReqDTO requestParam);

    /**
     * 批量创建短链接
     * @param requestParam 批量新增参数
     * @return 批量新增后的响应结果
     */
    Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);
}
