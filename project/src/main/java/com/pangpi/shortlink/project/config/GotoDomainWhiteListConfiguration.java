package com.pangpi.shortlink.project.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "short-link.goto-domain.white-list")
public class GotoDomainWhiteListConfiguration {

    /**
     * 是否开启白名单验证
     */
    private Boolean enable;

    /**
     * 可以跳转的网站集合
     */
    private String names;

    /**
     * 可跳转的原始链接域名
     */
    private List<String> details;

}
