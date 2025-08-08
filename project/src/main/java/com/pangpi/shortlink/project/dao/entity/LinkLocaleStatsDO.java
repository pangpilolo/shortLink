package com.pangpi.shortlink.project.dao.entity;

import java.util.Date;

import java.io.Serializable;

import com.pangpi.shortlink.convention.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (LinkLocaleStats)表实体类
 *
 * @author pangpi
 * @since 2024-07-05 20:55:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_locale_stats")
@Builder
public class LinkLocaleStatsDO extends BaseDO implements Serializable {
    //ID
    @TableId
    private Long id;

    //完整短链接
    private String fullShortUrl;
    //分组标识
    private String gid;
    //日期
    private Date date;
    //访问量
    private Integer cnt;
    //省份名称
    private String province;
    //市名称
    private String city;
    //城市编码
    private String adcode;
    //国家标识
    private String country;

}
