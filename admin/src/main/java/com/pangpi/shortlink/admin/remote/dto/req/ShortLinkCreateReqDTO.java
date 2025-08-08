package com.pangpi.shortlink.admin.remote.dto.req;

import lombok.AllArgsConstructor;
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
public class ShortLinkCreateReqDTO implements Serializable {

    //域名
    private String domain;

    //原始链接
    private String originUrl;

    //分组标识
    private String gid;

    //创建类型 0：控制台 1：接口
    private Integer createdType;

    //有效期类型 0：永久有效 1：用户自定义
    private Integer validDateType;

    //有效期
    private Date validDate;

    //描述
    private String describe;

}
