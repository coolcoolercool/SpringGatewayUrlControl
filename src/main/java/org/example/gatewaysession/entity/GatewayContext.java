package org.example.gatewaysession.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@ToString
public class GatewayContext {
    public static final String CACHE_GATEWAY_CONTEXT_KEY = "cacheGatewayContext";
    private Map<String, Object> sessionAttributes;
}