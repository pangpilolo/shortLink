package com.pangpi.shortlink.admin.common.constant;

public class WebConstants {

    /**
     * 用户登录后的token存活时间
     */
    public static final Long USER_LOGIN_EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    /**
     * 逻辑删除中未删除常量
     */
    public static final Integer NO_DELETE_FLAG = 0;

    /**
     * 逻辑删除中已删除常量
     */
    public static final Integer DELETE_FLAG = 1;
}
