package com.pangpi.shortlink.admin.dao.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.pangpi.shortlink.convention.base.BaseDO;
import lombok.*;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (Group)表实体类
 *
 * @author pangpi
 * @since 2024-06-27 20:11:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_group")
public class GroupDO extends BaseDO {
    //ID
    @TableId
    private Long id;

    //分组标识
    private String gid;
    //分组名称
    private String name;
    //创建分组用户名
    private String username;
    //分组排序
    private Integer sortOrder;

}
