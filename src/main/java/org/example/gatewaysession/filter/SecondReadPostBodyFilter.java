package org.example.gatewaysession.filter;


import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.entity.GatewayContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 验证从缓存中读取body
 */
@Slf4j
@Component
public class SecondReadPostBodyFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("----------------------------SecondReadPostBodyFilter Begin----------------------------");
        // 获取request body
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);

        log.info("gatewayContext:{}", gatewayContext);
        log.info("getFormData:{}", gatewayContext.getFormData());
        log.info("getFormData tokenValue:{}", gatewayContext.getFormData().getFirst("token"));
        log.info("----------------------------SecondReadPostBodyFilter end----------------------------");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
