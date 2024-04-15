package org.example.gatewaysession.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

//@Component
@Slf4j
public class ValidateFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerRequest serverRequest = new DefaultServerRequest(exchange);
        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
                Map<String, Object> bodyMap = decodeBody(body);
                log.info("ValidateFilter bodyMap:{}", bodyMap);
                Map<String, Object> newBodyMap = new HashMap<>();
                return Mono.just(encodeBody(bodyMap));
            }
            return Mono.empty();
        });

        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    long contentLength = headers.getContentLength();
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(super.getHeaders());
                    if (contentLength > 0) {
                        httpHeaders.setContentLength(contentLength);
                    } else {
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                    return httpHeaders;
                }

                @Override
                public Flux<DataBuffer> getBody() {
                    return outputMessage.getBody();
                }
            };
            return chain.filter(exchange.mutate().request(decorator).build());
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Map<String, Object> decodeBody(String body) {
        return Arrays.stream(body.split("&")).map(s -> s.split("=")).collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    private String encodeBody(Map<String, Object> map) {
        return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
    }
}


