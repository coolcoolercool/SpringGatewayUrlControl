package org.example.gatewaysession.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.config.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean verifyPermission(String url, String jwtStr) {
        try {
            //解析JWT字符串中的数据，并进行最基础的验证
            Claims claims = Jwts.parser()
                    .setSigningKey(authConfig.jwtSecretKey)
                    .parseClaimsJws(jwtStr)
                    .getBody();
            String token = claims.get(authConfig.authTokenKey).toString();

            // 从redis中获取 key为 token的键值对
            return Boolean.TRUE.equals(redisTemplate.hasKey(token));
        } catch (ExpiredJwtException e) {
            // 已过期令牌
            log.error("token is expired, err msg: {}", e.getMessage());
        } catch (SignatureException e) {
            // 伪造令牌
            log.error("token Signature is error, err msg: {}", e.getMessage());
        } catch (Exception e) {
            // 系统错误
            log.error("server inner error, err msg: {}", e.getMessage());
        }
        return false;
    }
}
