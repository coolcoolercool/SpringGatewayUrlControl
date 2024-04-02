package org.example.gatewaysession.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;

@Slf4j
@Component
public class RedirectToFilter implements GlobalFilter, Order {

    final String REDIRECT_TOKEN = "redirect_to";

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("RedirectToFilter filter");
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String token = exchange.getRequest().getHeaders().getFirst(REDIRECT_TOKEN);
        log.info("RedirectToFilter filter token:{}", token);

        if (token != null && token.equals("1")) {
            String urlToRedirectTo = "https://www.baidu.com/";
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(HttpHeaders.LOCATION, urlToRedirectTo);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int value() {
        return -1;
    }
}
