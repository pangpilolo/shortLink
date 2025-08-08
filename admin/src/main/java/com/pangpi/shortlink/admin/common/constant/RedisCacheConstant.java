package com.pangpi.shortlink.admin.common.constant;

/**
 * 短链接后台管理的redis缓存key的常量
 */
public class RedisCacheConstant {

    public static final String LOCK_USER_REGISTER_KEY = "short-link:user_name_register:";

    public static final String LOCK_USER_LOGIN_KEY = "short-link:user_name_login:";

    public static final String USER_LOGIN_KEY = "short-link:user_login:";

    /**
     * 分组创建分布式锁
     */
    public static final String LOCK_GROUP_CREATE_KEY = "short-link:lock_group-create:%s";

}
