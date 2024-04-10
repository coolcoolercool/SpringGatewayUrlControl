package org.example.gatewaysession.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Slf4j
public class AuthConfig {
    @Value("${jwt.secret.key}")
    public String jwtSecretKey;

    @Value("${jwt.secret.auth-token-key}")
    public String authTokenKey;

    @Value("${jwt.secret.auth-login-url}")
    public String authLoginUrl;

    public Map<String, String> pathFunctionMap = new HashMap<>();

    // TODO: 需要在gateway启动的时候，在系统管理平台调用，获取 path 和 权限点的映射关系
    public AuthConfig() {
        String function = "function_id_0";
        String path = "/test/**";
        pathFunctionMap.put(path, function);

        function = "function_id_0";
        path = "/gw/**";
        pathFunctionMap.put(path, function);
        log.info("AuthConfig pathFunctionMap:{}", pathFunctionMap);
    }
}
