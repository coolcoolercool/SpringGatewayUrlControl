package org.example.gatewaysession.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.List;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Order {

    private String HASH_SESSION_KEY_PREFIX = "spring:session:sessions:";

    private String TOKEN_HEADER_KEY = "auth_token";

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("----------------------------AuthFilter Begin----------------------------");
        String url = exchange.getRequest().getURI().getPath();
        log.info("filter url: {}", url);

        ServerHttpResponse response = exchange.getResponse();
        String token = exchange.getRequest().getHeaders().getFirst(TOKEN_HEADER_KEY);
        if (StringUtils.isEmpty(token)) {
            log.error("token is null");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        String hashKey = HASH_SESSION_KEY_PREFIX + token;
        log.info("hashKey: {}", hashKey);
        String userInfoStr = (String) redisTemplate.opsForHash().get(hashKey, "sessionAttr:sessionKey");
        log.info("userInfoStr: {}", userInfoStr);

        ObjectMapper objectMapper = new ObjectMapper();
        UserInfo userIno = null;
        try {
            userIno = objectMapper.readValue(userInfoStr, UserInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("UserInfo: {}", userIno);

        boolean isMatch = isMatchUrl(url, userIno.getUrlList());
        log.info("isMatcher: {}", isMatch);
        if (!isMatch) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        log.info("----------------------------AuthFilter End----------------------------");
        return chain.filter(exchange);
    }

    private boolean isMatchUrl(String url, List<String> urlPattern) {
        boolean isMatch = false;
        for (String pattern : urlPattern) {
            if (antPathMatcher.isPattern(pattern)) {
                log.info("url:{} isPattern", pattern);
                isMatch = antPathMatcher.match(pattern, url);
            } else {
                log.info("url:{} is not pattern", pattern);
                isMatch = url.equals(pattern);
            }

            if (isMatch) {
                log.info("url: {}", pattern);
                return isMatch;
            }
        }
        return isMatch;
    }

    @Override
    public int value() {
        return 100;
    }
}
