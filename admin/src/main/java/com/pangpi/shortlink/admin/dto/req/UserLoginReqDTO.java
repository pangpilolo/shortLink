package com.pangpi.shortlink.admin.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginReqDTO implements Serializable {

    //用户名
    private String username;
    //密码
    private String password;

}
