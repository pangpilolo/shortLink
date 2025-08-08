package com.pangpi.shortlink.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.pangpi.shortlink.convention.util.JWTUtils;
import com.pangpi.shortlink.gateway.config.Config;
import com.pangpi.shortlink.gateway.dto.GatewayErrorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            String requestMethod = request.getMethod().toString();
            // 判断是否是白名单
            if (!isPathInWhiteList(requestPath, requestMethod, config.getWhitePathList())) {
                // 从请求头获取token解析
                String token = request.getHeaders().getFirst("token");
                String username = JWTUtils.getUsername(token);
                String userInfo = stringRedisTemplate.opsForValue().get("short-link:user_login:" + username);
                if (StrUtil.isNotBlank(userInfo)) {
                    JSONObject userInfoJsonObject = JSON.parseObject(userInfo);
                    ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                        httpHeaders.set("userId", userInfoJsonObject.getString("id"));
                        httpHeaders.set("realName", URLEncoder.encode(userInfoJsonObject.getString("realName"), StandardCharsets.UTF_8));
                        httpHeaders.set("username", URLEncoder.encode(userInfoJsonObject.getString("username"), StandardCharsets.UTF_8));
                    });
                    return chain.filter(exchange.mutate().request(builder.build()).build());
                }
                // 如果不在白名单中，同时还没有携带token，则直接返回未验证的status
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    GatewayErrorResult resMessage = GatewayErrorResult.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Token validation error")
                            .build();
                    return bufferFactory.wrap(JSON.toJSONString(resMessage).getBytes(StandardCharsets.UTF_8));
                }));
            }
            return chain.filter(exchange);
        });
    }


    public boolean isPathInWhiteList(String path, String requestMethod, List<String> whiteList) {
        return (!CollUtil.isEmpty(whiteList) && whiteList.stream().anyMatch(path::startsWith) || (path.equals("/api/shortlink/admin/v1/user") && ObjUtil.equals(requestMethod, "POST")));
    }
}
