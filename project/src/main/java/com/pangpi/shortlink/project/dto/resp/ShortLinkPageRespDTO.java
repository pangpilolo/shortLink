package com.pangpi.shortlink.project.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.pangpi.shortlink.project.dao.entity.ShortLinkDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接分页查询返回实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkPageRespDTO extends Page<ShortLinkDO> {

    //ID
    private Long id;

    //域名
    private String domain;
    //短链接
    private String shortUri;
    //完整短链接
    private String fullShortUrl;
    //原始链接
    private String originUrl;
    //分组标识
    private String gid;
    //有效期类型 0：永久有效 1：用户自定义
    private Integer validDateType;
    //有效期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;
    //描述
    private String describe;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
