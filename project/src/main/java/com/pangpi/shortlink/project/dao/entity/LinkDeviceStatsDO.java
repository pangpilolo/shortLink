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
 * (LinkDeviceStats)表实体类
 *
 * @author pangpi
 * @since 2024-07-06 19:36:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_device_stats")
@Builder
public class LinkDeviceStatsDO extends BaseDO implements Serializable {
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
    //访问设备
    private String device;

}
