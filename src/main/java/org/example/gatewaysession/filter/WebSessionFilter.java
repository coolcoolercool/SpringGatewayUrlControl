package org.example.gatewaysession.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.entity.GatewayContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.util.Map;

//@Component
@Slf4j
public class WebSessionFilter implements GlobalFilter, Order {
    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("WebSessionFilter filter begin");
        return exchange.getSession().flatMap(webSession -> {
            GatewayContext gatewayContext = new GatewayContext();
            Map<String, Object> sessionMap = webSession.getAttributes();
            gatewayContext.setSessionAttributes(sessionMap);

            ServerHttpRequest request = exchange.getRequest();

            return chain.filter(exchange);
        }).then(Mono.fromRunnable(() -> {
            log.info("this is a post filter");
        }));

    }

    @Override
    public int value() {
        return 0;
    }
}
