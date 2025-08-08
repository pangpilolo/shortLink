package com.pangpi.shortlink.project.dao.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.pangpi.shortlink.convention.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (Link)表实体类
 *
 * @author pangpi
 * @since 2024-06-28 16:20:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link")
@Builder
public class ShortLinkDO extends BaseDO {
    //ID
    @TableId
    private Long id;

    //域名
    private String domain;
    //短链接
    private String shortUri;
    //完整短链接
    private String fullShortUrl;
    //原始链接
    private String originUrl;
    //点击量
    private Integer clickNum;
    //分组标识
    private String gid;
    //网站图标
    private String favicon;
    //启用标识 0：未启用 1：已启用
    private Integer enableStatus;
    //创建类型 0：控制台 1：接口
    private Integer createdType;
    //有效期类型 0：永久有效 1：用户自定义
    private Integer validDateType;
    //有效期
    private Date validDate;
    //描述
    @TableField(value = "`describe`")
    private String describe;

}
