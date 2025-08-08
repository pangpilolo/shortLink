package com.pangpi.shortlink.project.service;
 
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pangpi.shortlink.project.dao.entity.ShortLinkDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pangpi.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;


/**
 * (Link)表服务接口
 * @author pangpi
 * @since 2024-06-28 16:20:42
 */
public interface ShortLinkService extends IService<ShortLinkDO> {

    /**
     * 创建新的短链接
     * @param requestParam 短链接新增参数
     * @return 新增后的短链接返回对象
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    /**
     * 批量创建短链接
     * @param requestParam 批量新增参数
     * @return 批量新增后的响应结果
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     * @param requestParam 请求参数
     * @return 分页响应对象
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 统计分组下的短链接个数
     * @param requestParam gid集合
     * @return 各个分组短链接个数
     */
    List<ShortLinkGroupCountRespDTO> listShortLinkGroupCount(List<String> requestParam);

    /**
     * 修改短链接
     * @param requestParam 修改短链接参数
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 根据短链接跳转
     * @param shortUrl 短链接
     * @param request 请求对象
     * @param response 响应对象
     */
    void restoreUrl(String shortUrl, HttpServletRequest request, HttpServletResponse response);

    /**
     * 短链接统计
     *
     * @param shortLinkStatsRecord 短链接统计实体参数
     */
    void shortLinkStats(ShortLinkStatsRecordDTO shortLinkStatsRecord);
}
