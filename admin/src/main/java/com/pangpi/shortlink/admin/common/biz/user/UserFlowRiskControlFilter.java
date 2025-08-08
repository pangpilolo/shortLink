package com.pangpi.shortlink.admin.common.biz.user;


import com.alibaba.fastjson2.JSON;
import com.pangpi.shortlink.admin.config.UserFlowRiskControlConfiguration;
import com.pangpi.shortlink.convention.errorcode.BaseErrorCode;
import com.pangpi.shortlink.convention.result.Results;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UserFlowRiskControlFilter implements Filter {


    private final StringRedisTemplate redisTemplate;

    private final UserFlowRiskControlConfiguration userFlowRiskControlConfiguration;

    private static final String USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH = "lua/user_flow_risk_control.lua";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 创建一个redis的可执行脚本,泛型是表示最后返回的值的类型
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH)));
        script.setResultType(Long.class);
        String username = Optional.ofNullable(UserContext.getUsername()).orElse("other");
        Long result = null;
        try {
            result = redisTemplate.execute(script, List.of(username), userFlowRiskControlConfiguration.getTimeWindow());
        } catch (Throwable ex) {
            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(BaseErrorCode.FLOW_LIMIT_ERROR)));
            log.error("用户限流执行lua脚本出错", ex);
        }
        if (result == null || result > userFlowRiskControlConfiguration.getMaxAccessCount()) {
            returnJson((HttpServletResponse) servletResponse, JSON.toJSONString(Results.failure(BaseErrorCode.FLOW_LIMIT_ERROR)));
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }



    private void returnJson(HttpServletResponse response, String json) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter out = response.getWriter()) {
            out.write(json);
        }
    }
}
