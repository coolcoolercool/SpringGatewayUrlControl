package org.example.gatewaysession.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.gatewaysession.util.CommonUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;

@Slf4j
public class AuthenticationFilter implements GlobalFilter, Order  {

    /**
     * The URL to the CAS Server login.
     */
    // TODO: 添加到配置文件中
    private String casServerLoginUrl = "http://my.test.com:8090/cas/login";

    /**
     * Whether to send the renew request or not.
     */
    private boolean renew = false;
    /**
     * The method used by the CAS server to send the user back to the application.
     */
    private String method;

    /**
     * Protocol 的参数
     */
    String artifactParameterName = "ticket";
    String serviceParameterName = "service";
    Boolean isEncodeServiceUrl = false;
    String serverName = "";
    String service = "";

    Boolean isGateway = false;

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // TODO: 跳过白名单url

        // 第一次请求，serviceUrl: http://my.test.com:9091/demo1/set
        String serviceUrl = constructServiceUrl(request, response);

        // 第一次请求，ticket为""
        String ticket = retrieveTicketFromRequest(request);

        // 第一次请求，wasGatewayed
        // TODO: 这里默认 wasGatewayed 为 false
        final boolean wasGatewayed = isGateway;

        if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
            return chain.filter(exchange);
        }

        final String modifiedServiceUrl = serviceUrl;
        log.debug("no ticket and no assertion found");

        // TODO: wasGatewayed 默认为fasle，因此这里 modifiedServiceUrl = serviceUrl
/*        if (this.gateway) {
            logger.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }*/
        log.debug("Constructed service url: {}", modifiedServiceUrl);

        // 第一次请求: 这里urlToRedirectTo: http://my.test.com:8090/cas/login
        final String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.casServerLoginUrl,
                serviceParameterName, modifiedServiceUrl, this.renew, isGateway, this.method);
        log.debug("redirecting to \"{}\"", urlToRedirectTo);

        response.setStatusCode(HttpStatus.SEE_OTHER); // 重定向 303
        response.getHeaders().set(HttpHeaders.LOCATION, urlToRedirectTo);
        return response.setComplete();
    }

    /**
     * 一个模式匹配器，它在 url 内部查找模式，即
     *  假定已通过正则表达式语法指定。
     *  匹配行为基于 {@link Matcher#matches()}：
     *  尝试将整个区域与模式进行匹配。
     * @param request
     * @return
     */
/*    private boolean isRequestUrlExcluded(final HttpServletRequest request) {
        final StringBuffer urlBuffer = request.getRequestURL();
        if (request.getQueryString() != null) {
            urlBuffer.append("?").append(request.getQueryString());
        }
        final String requestUri = urlBuffer.toString();
        return this.ignoreUrlPatternMatcherStrategyClass.matches(requestUri);
    }*/

    protected final String constructServiceUrl(final ServerHttpRequest request, final ServerHttpResponse response) {
        return CommonUtils.constructServiceUrl(request, response, this.service, this.serverName,
                serviceParameterName, artifactParameterName, isEncodeServiceUrl);
    }


    protected String retrieveTicketFromRequest(final ServerHttpRequest request) {
        return CommonUtils.safeGetParameter(request, artifactParameterName, Arrays.asList(serviceParameterName));
    }

    @Override
    public int value() {
        return 10;
    }
}
