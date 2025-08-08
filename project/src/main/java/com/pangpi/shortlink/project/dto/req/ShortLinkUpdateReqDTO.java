package com.pangpi.shortlink.project.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ShortLinkUpdateReqDTO implements Serializable {

    //原始链接
    private String originUrl;

    // 原本的短链接
    private String fullShortUrl;

    //分组标识
    private String gid;

    private String originGid;

    //创建类型 0：控制台 1：接口
    private Integer createdType;

    //有效期类型 0：永久有效 1：用户自定义
    private Integer validDateType;

    //有效期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date validDate;

    //描述
    private String describe;

}
