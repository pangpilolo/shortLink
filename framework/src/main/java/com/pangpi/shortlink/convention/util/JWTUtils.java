package com.pangpi.shortlink.convention.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTHeader;
import cn.hutool.jwt.JWTUtil;
import java.util.*;


public class JWTUtils {

    //有效期为 一个小时
    public static final Long JWT_TTL = 60 * 60 * 1000L;


    /**
     * 生成jtw
     * @param username token中要存放的数据（json格式）
     * @return jwt串
     */
    public static String createJWT(String username) {
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("username", username);
                put("expire_time", JWT_TTL);
            }
        };

        return JWTUtil.createToken(map, "1234".getBytes());
    }

    /**
     * 解析token获得用户名
     * @param token token
     * @return username
     */
    public static String getUsername(String token) {
        final JWT jwt = JWTUtil.parseToken(token);
        jwt.getHeader(JWTHeader.TYPE);
        return (String) jwt.getPayload("username");
    }

}
