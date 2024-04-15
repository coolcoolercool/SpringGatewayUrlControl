package org.example.gatewaysession.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 验证第一次读取请求中的body
 */
@Slf4j
@Component
public class FirstReadPostBodyFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("----------------------------FirstReadPostBodyFilter Begin----------------------------");
        ServerHttpRequest httpRequest = exchange.getRequest();
        // 获取请求体
        Flux<DataBuffer> body = httpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        String bodyStr = bodyRef.get();
        // 打印请求体
        log.info("body:{}", bodyStr);
        log.info("----------------------------FirstReadPostBodyFilter end----------------------------");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
