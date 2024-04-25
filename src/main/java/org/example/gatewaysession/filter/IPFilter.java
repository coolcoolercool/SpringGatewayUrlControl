package org.example.gatewaysession.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.gatewaysession.config.BlackIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class IPFilter implements GlobalFilter, Ordered {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";

    private static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";
    private static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private BlackIP blackIP;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("----------------------------IPFilter Begin----------------------------");
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        String clientIP = getRealIpAddress(request);
        log.info("clientIP: {}", clientIP);

        if (isBlackIP(clientIP)) {
            log.error("IPFilter block clientIP: {}", clientIP);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        log.info("----------------------------IPFilter End----------------------------");
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
    public int getOrder() {
        return 0;
    }

    /**
     * 获取真实客户端IP
     * @param serverHttpRequest
     * @return
     */
    private String getRealIpAddress(ServerHttpRequest serverHttpRequest) {
        String ipAddress;
        try {
            // 1.根据常见的代理服务器转发的请求ip存放协议，从请求头获取原始请求ip。值类似于203.98.182.163, 203.98.182.163
            ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_X_FORWARDED_FOR);
            if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_PROXY_CLIENT_IP);
            }
            if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_WL_PROXY_CLIENT_IP);
            }

            // 2.如果没有转发的ip，则取当前通信的请求端的ip
            if (StringUtils.isEmpty(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                InetSocketAddress inetSocketAddress = serverHttpRequest.getRemoteAddress();
                if(inetSocketAddress != null) {
                    ipAddress = inetSocketAddress.getAddress().getHostAddress();
                }
                // 如果是127.0.0.1，则取本地真实ip
//                if(LOCALHOST.equals(ipAddress) || "0:0:0:0:0:0:0:1".equalsIgnoreCase(ipAddress)){
                //根据网卡取本机配置的IP
//                    InetAddress inet = InetAddress.getLocalHost();
//                    ipAddress= inet.getHostAddress();
//                }
            }


            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***"
            if (ipAddress != null) {
                ipAddress = ipAddress.split(SEPARATOR)[0].trim();
            }
        } catch (Exception e) {
            log.error("解析请求IP失败", e);
            ipAddress = "";
        }
        return ipAddress == null ? "" : ipAddress;
    }


}
