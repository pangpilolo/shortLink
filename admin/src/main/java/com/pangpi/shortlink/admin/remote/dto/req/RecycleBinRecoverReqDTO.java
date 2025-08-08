package com.pangpi.shortlink.admin.remote.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 短链接回收站回复功能
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinRecoverReqDTO implements Serializable {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 全部短链接
     */
    private String fullShortUrl;
}
