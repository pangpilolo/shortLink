package com.pangpi.shortlink.project.common.enums;

import com.pangpi.shortlink.convention.errorcode.IErrorCode;

public enum ShortLinkErrorCodeEnum implements IErrorCode {
    ORIGIN_URL_NOT_BLANK("B000300", "原始链接不能为空"),
    DOMAIN_NOT_BLANK("B000301", "域名不能为空"),
    GENERATE_SO_MUCH("B000302", "短链接频繁生成，请稍后再试"),
    SHORT_LINK_URL_NOT_FOUND("B000303", "短链接不存在"),
    ;


    private final String code;

    private final String message;

    ShortLinkErrorCodeEnum(String code, String message) {
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
