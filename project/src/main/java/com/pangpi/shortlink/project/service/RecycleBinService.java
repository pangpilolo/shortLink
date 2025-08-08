package com.pangpi.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pangpi.shortlink.project.dao.entity.ShortLinkDO;
import com.pangpi.shortlink.project.dto.req.RecycleBinRecoverReqDTO;
import com.pangpi.shortlink.project.dto.req.RecycleBinRemoveReqDTO;
import com.pangpi.shortlink.project.dto.req.RecycleBinSaveReqDTO;
import com.pangpi.shortlink.project.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.pangpi.shortlink.project.dto.resp.ShortLinkPageRespDTO;

/**
 * 短链接回收站管理接口
 */
public interface RecycleBinService extends IService<ShortLinkDO> {

    /**
     * 将短链接保存到回收站
     * @param requestParam 请求参数
     */
    void saveToRecycleBin(RecycleBinSaveReqDTO requestParam);

    /**
     * 分页查询回收站的短链接
     * @param requestParam 请求参数
     * @return 分页短链接集合
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam);

    /**
     * 将短链接从回收站恢复
     * @param requestParam 请求参数
     */
    void recover(RecycleBinRecoverReqDTO requestParam);

    /**
     * 将短链接删除
     * @param requestParam 请求参数
     */
    void removeShortLink(RecycleBinRemoveReqDTO requestParam);
}
