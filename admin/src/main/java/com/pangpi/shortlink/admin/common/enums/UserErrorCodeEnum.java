package com.pangpi.shortlink.admin.common.enums;

import com.pangpi.shortlink.convention.errorcode.IErrorCode;

public enum UserErrorCodeEnum implements IErrorCode {
    USER_NOT_FOUND("B000200", "用户记录不存在"),
    USER_NAME_EXIST("B000201", "用户名已存在"),
    USER_SAVE_ERROR("B000202", "用户注册失败"),
    USER_NAME_EMPTY("B000203", "用户名为空"),
    USER_PASSWORD_EMPTY("B000204", "用户注册密码为空"),
    USER_EMAIL_EMPTY("B000205", "用户注册邮箱为空"),
    USER_PHONE_EMPTY("B000206", "用户注册手机号为空"),
    USER_LOGIN_ERROR("B000207", "用户登录异常"),
    USER_NOT_FOUND_OR_NOT_LOGIN("B000208", "用户不存在或未登录"),
    ;


    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
