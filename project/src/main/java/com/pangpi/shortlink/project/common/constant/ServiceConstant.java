package com.pangpi.shortlink.project.common.constant;

public class ServiceConstant {
    /**
     * 逻辑删除中未删除常量
     */
    public static final Integer NO_DELETE_FLAG = 0;

    /**
     * 逻辑删除中已删除常量
     */
    public static final Integer DELETE_FLAG = 1;

    /**
     * 短链接启用状态：启用
     */
    public static final Integer ENABLE_STATUS_OPEN = 1;
    /**
     * 短链接启用状态：关闭
     */
    public static final Integer ENABLE_STATUS_CLOSE = 0;

    /**
     * 短链接默认一个月的缓存有效期
     */
    public static final long DEFAULT_CACHE_VALID_TIME = 2626560000L;

    public static final String AMAP_REMOTE_URL = "https://restapi.amap.com/v3/ip";

}
