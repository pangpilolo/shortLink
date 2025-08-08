package com.pangpi.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateReqDTO implements Serializable {

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

}
