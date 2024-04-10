package org.example.gatewaysession.service;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.config.AuthConfig;
import org.example.gatewaysession.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AuthService {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // TODO: 与俊杰约定好存放的key即可
    final String REDIS_USER_INFO_KEY = "functionList";
    final String JWT_REDIS_KEY = "jwt_redis_key";

    public boolean verifyPermission(String url, String jwtStr) {
        try {
            // 解析JWT字符串中的数据，并进行最基础的验证
            String uuidRedisKey = JwtUtils.verifyToken(jwtStr, JWT_REDIS_KEY, authConfig.jwtSecretKey);

            // 从redis中获取键为 uuidRedisKey，value预期中是用户信息，主要获取的是用户的权限点的List
            String userInfoStr = redisTemplate.opsForValue().get(uuidRedisKey);
            log.info("verifyPermission userInfo:{}", userInfoStr);

            // 使用fastjson解析json字符串用户信息成map
            List<String> functionList = (List<String>) ((Map) JSON.parse(userInfoStr)).get(REDIS_USER_INFO_KEY);
            log.info("userInfo functions:{}", functionList);
            return checkPathAndFunction(functionList, url);
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

    private boolean checkPathAndFunction(List<String> functionList, String path) {
        // 匹配path，如果匹配上，则检查 List<String> functionList 是否包含 function
        boolean isMatch = false;
        for (Map.Entry<String, String> entry : authConfig.pathFunctionMap.entrySet()) {
            String pathPattern = entry.getKey();
            if (antPathMatcher.isPattern(pathPattern)) {
                isMatch = antPathMatcher.match(pathPattern, path);
            } else {
                isMatch = path.equals(pathPattern);
            }

            if (isMatch && functionList.contains(entry.getValue())) {
                return true;
            }
        }
        return false;
    }
}
