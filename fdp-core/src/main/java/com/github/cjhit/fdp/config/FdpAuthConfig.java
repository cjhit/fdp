package com.github.cjhit.fdp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 权限配置
 * 备注：如果没明确指定策略的，则登录即可访问
 */
@Data
@Component
@ConfigurationProperties(prefix = "auth.config")
public class FdpAuthConfig {

    private String[] token = {}; //需要进拦截器的集合列表
    private String[] ignore = {}; //不需要鉴权的url集合
    private String[] urlPermission = {};//需要token，且用户需要有该权限的url集合
}
