package org.example.gatewaysession.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "jwt")
public class AuthConfig {
    @Value("${jwt.secret.key}")
    public String jwtSecretKey;

    @Value("${jwt.secret.auth-token-key}")
    public String authTokenKey;

    @Value("${jwt.secret.auth-login-url}")
    public String authLoginUrl;
}
