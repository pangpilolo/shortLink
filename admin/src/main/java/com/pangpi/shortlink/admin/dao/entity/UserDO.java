package com.pangpi.shortlink.admin.dao.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.pangpi.shortlink.convention.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (User)表实体类
 * @author pangpi
 * @since 2024-06-23 20:25:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class UserDO extends BaseDO {
    //ID
    @TableId
    private Long id;

    //用户名
    private String username;
    //密码
    private String password;
    //真实姓名
    private String realName;
    //手机号
    private String phone;
    //邮箱
    private String mail;
    //注销时间戳
    private Long deletionTime;

}
