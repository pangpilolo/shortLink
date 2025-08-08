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
 * (LinkAccessStats)表实体类
 *
 * @author pangpi
 * @since 2024-07-04 20:05:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_access_stats")
@Builder
public class LinkAccessStatsDO extends BaseDO implements Serializable {
    //ID
    @TableId
    private Long id;

    //完整短链接
    private String fullShortUrl;
    //日期
    private Date date;
    //访问量
    private Integer pv;
    //独立访客数
    private Integer uv;
    //独立IP数
    private Integer uip;
    //小时
    private Integer hour;
    //星期
    private Integer weekday;

}
