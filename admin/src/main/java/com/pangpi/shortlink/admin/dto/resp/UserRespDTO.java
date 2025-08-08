package com.pangpi.shortlink.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pangpi.shortlink.admin.common.serializer.PhoneDesensitizationSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRespDTO implements Serializable {

    //ID
    private Long id;
    //用户名
    private String username;
    //真实姓名
    private String realName;
    //手机号
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;
    //邮箱
    private String mail;

}
