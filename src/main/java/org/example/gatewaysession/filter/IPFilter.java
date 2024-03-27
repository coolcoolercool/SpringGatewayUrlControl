package org.example.gatewaysession.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.config.BlackIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class IPFilter implements GlobalFilter, Order {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private BlackIP blackIP;

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        String clientIP = Objects.requireNonNull(request.getRemoteAddress()).getHostName();
        log.info("clientIP: {}", clientIP);

        if (isBlackIP(clientIP)) {
            log.error("IPFilter block clientIP: {}", clientIP);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    private boolean isBlackIP(String ip) {
        List<String> blackIPList = blackIP.getIpList();
        if (blackIPList.isEmpty()) {
            log.info("blackList is empty");
            return false;
        }
        return blackIPList.stream().anyMatch(action -> antPathMatcher.match(action, ip));
    }

    @Override
    public int value() {
        return 0;
    }
}
