package com.pangpi.shortlink.project.dao.entity;


import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (LinkGoto0)表实体类
 *
 * @author pangpi
 * @since 2024-07-01 19:41:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_goto")
public class LinkGotoDO implements Serializable {
    //ID
    @TableId
    private Long id;

    //分组标识
    private String gid;
    //完整短链接
    private String fullShortUrl;

}
