package org.example.gatewaysession.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@ToString
public class GatewayContext {
    public static final String CACHE_GATEWAY_CONTEXT = "cacheGatewayContext";
    private Map<String, Object> sessionAttributes;
    private MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    /**
     * cache json body
     */
    private String cacheBody;
    /**
     * path
     */
    private String path;
    /**
     * cache headers
     */
    private HttpHeaders headers;
    /**
     * ipAddress
     */
    private String  ipAddress;
}