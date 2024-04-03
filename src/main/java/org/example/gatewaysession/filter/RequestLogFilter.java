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
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;

@Slf4j
//@Component
public class RequestLogFilter implements GlobalFilter, Order {
    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("RedirectToFilter filter");
        ServerHttpRequest request = exchange.getRequest();

        String url = request.getURI().getPath();
        log.info("RequestLogFilter filter url:{}", url); // url: /test/autoSession/

        String requestMethod = request.getMethod().name();
        log.info("RequestLogFilter filter requestMethod:{}", requestMethod); // requestMethod: GET

        StringBuilder headContent = new StringBuilder();
        HttpHeaders headers = request.getHeaders();
        headers.forEach((headerName, headerValue) -> {
            headContent.append("===Headers=== {}: {}\n");
            headContent.append(headerName);
            headContent.append(StringUtils.join(headerValue));
        });
        log.info("RequestLogFilter filter headContent:{}", headContent);

        // 查询参数的kv map
        MultiValueMap<String, String> queryParamsMap = request.getQueryParams();
        log.info("RequestLogFilter filter queryParamsMap:{}", queryParamsMap);

        // uriQuery: redirect_to_params=1&auth_token_params=ac01d8c1-0672-44ff-8725-5c0aa08a5463
        String uriQuery = request.getURI().getQuery();
        log.info("RequestLogFilter filter uriQuery:{}", uriQuery);

        return chain.filter(exchange);
    }

    @Override
    public int value() {
        return -1;
    }
}
