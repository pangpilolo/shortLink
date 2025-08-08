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
 * (LinkAccessLogs)表实体类
 *
 * @author pangpi
 * @since 2024-07-06 19:00:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_access_logs")
@Builder
public class LinkAccessLogsDO extends BaseDO implements Serializable {
    //ID
    @TableId
    private Long id;

    //完整短链接
    private String fullShortUrl;
    //分组标识
    private String gid;
    //用户信息
    private String user;
    //浏览器
    private String browser;
    //操作系统
    private String os;
    //IP
    private String ip;

}
