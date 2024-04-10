package org.example.gatewaysession.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.config.AuthConfig;
import org.example.gatewaysession.config.SkipUrl;
import org.example.gatewaysession.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private SkipUrl skipUrl;

    private String TOKEN_HEADER_NAME = "Authorization";

    private AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String url = request.getURI().getPath();
        if (isWhiteUrl(url)) {
            log.info("url:{} is in white url, no need check", url);
            return chain.filter(exchange);
        }

        String jwtStr = request.getHeaders().getFirst(TOKEN_HEADER_NAME);
        if (jwtStr == null || jwtStr.isEmpty() || !authService.verifyPermission(url, jwtStr)) {
            log.info("Authorization in headers is null, redirecting to:{}", authConfig.authLoginUrl);
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            response.getHeaders().set(HttpHeaders.LOCATION, authConfig.authLoginUrl);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isWhiteUrl(String url) {
        List<String> whiteUrlList = skipUrl.getUrlList();
        if (whiteUrlList.isEmpty()) {
            log.info("whiteUrl is empty");
            return false;
        }
        return whiteUrlList.stream().anyMatch(action -> antPathMatcher.match(action, url));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
