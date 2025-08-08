package com.pangpi.shortlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (Link)表实体类
 *
 * @author pangpi
 * @since 2024-06-28 16:20:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkCreateRespDTO implements Serializable {

    /**
     * 分组信息
     */
    private ShortLinkGroupRespDTO group;

    /**
     * 域名
     */
    private String domain;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 短链接
     */

    private String fullShortUrl;

}
