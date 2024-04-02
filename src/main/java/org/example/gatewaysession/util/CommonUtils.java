package org.example.gatewaysession.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CommonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * Url encode a value using UTF-8 encoding.
     *
     * @param value the value to encode.
     * @return the encoded value.
     */
    public static String urlEncode(final String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs a service url from the HttpServletRequest or from the given
     * serviceUrl. Prefers the serviceUrl provided if both a serviceUrl and a
     * serviceName.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @param service the configured service url (this will be used if not null)
     * @param serverNames the server name to  use to construct the service url if the service param is empty.  Note, prior to CAS Client 3.3, this was a single value.
     *           As of 3.3, it can be a space-separated value.  We keep it as a single value, but will convert it to an array internally to get the matching value. This keeps backward compatability with anything using this public
     *           method.
     * @param serviceParameterName the service parameter name to remove (i.e. service)
     * @param artifactParameterName the artifact parameter name to remove (i.e. ticket)
     * @param encode whether to encode the url or not (i.e. Jsession).
     * @return the service url to use.
     */
    public static String constructServiceUrl(final ServerHttpRequest request, final ServerHttpResponse response,
                                             final String service, final String serverNames, final String serviceParameterName,
                                             final String artifactParameterName, final boolean encode) {
        // service: null, serverNames: http://my.test.com:9091, serviceParameterName: service, artifactParameterName: ticket
        // TODO: encode 暂定为false
        /*if (CommonUtils.isNotBlank(service)) {
            return encode ? response.encodeURL(service) : service;
        }*/

        final String serverName = findMatchingServerName(request, serverNames);
        // 第一次登录encode为true
        final URIBuilder originalRequestUrl = new URIBuilder(request.getURI().getPath(), encode);
        // 第一次登录请求这里为null
        originalRequestUrl.setParameters(request.getQueryString());

        final URIBuilder builder;
        if (!serverName.startsWith("https://") && !serverName.startsWith("http://")) {
            // TODO: 这里不用 https
            // final String scheme = request.isSecure() ? "https://" : "http://";
            final String scheme = "http://";
            builder = new URIBuilder(scheme + serverName, encode);
        } else {
            builder = new URIBuilder(serverName, encode);
        }

        if (builder.getPort() == -1 && !requestIsOnStandardPort(request)) {
            builder.setPort(Objects.requireNonNull(request.getRemoteAddress()).getPort());
        }

        // builder.getEncodedPath() + request.getURI().getPath(): /demo1/set
        builder.setEncodedPath(builder.getEncodedPath() + request.getURI().getPath());

        final List<String> serviceParameterNames = Arrays.asList(serviceParameterName.split(","));
        if (!serviceParameterNames.isEmpty() && !originalRequestUrl.getQueryParams().isEmpty()) {
            for (final URIBuilder.BasicNameValuePair pair : originalRequestUrl.getQueryParams()) {
                final String name = pair.getName();
                if (!name.equals(artifactParameterName) && !serviceParameterNames.contains(name)) {
                    if (name.contains("&") || name.contains("=")) {
                        final URIBuilder encodedParamBuilder = new URIBuilder();
                        encodedParamBuilder.setParameters(name);
                        for (final URIBuilder.BasicNameValuePair pair2 : encodedParamBuilder.getQueryParams()) {
                            final String name2 = pair2.getName();
                            if (!name2.equals(artifactParameterName) && !serviceParameterNames.contains(name2)) {
                                builder.addParameter(name2, pair2.getValue());
                            }
                        }
                    } else {
                        builder.addParameter(name, pair.getValue());
                    }
                }
            }
        }

        final String result = builder.toString();
        // TODO: encode 为true，但是加密前后的 result 和 returnValue 是一样的值
        //  encodeURL的作用: https://openhome.cc/Gossip/ServletJSP/EncodeURL.html
        // final String returnValue = encode ? response.encodeURL(result) : result;
        final String returnValue = result;
        LOGGER.debug("serviceUrl generated: {}", returnValue);
        return returnValue;
    }

    /**
     * Safe method for retrieving a parameter from the request without disrupting the reader UNLESS the parameter
     * actually exists in the query string.
     * <p>
     * Note, this does not work for POST Requests for "logoutRequest".  It works for all other CAS POST requests because the
     * parameter is ALWAYS in the GET request.
     * <p>
     * If we see the "logoutRequest" parameter we MUST treat it as if calling the standard request.getParameter.
     * <p>
     *     Note, that as of 3.3.0, we've made it more generic.
     * </p>
     *
     * @param request the request to check.
     * @param parameter the parameter to look for.
     * @return the value of the parameter.
     */
    public static String safeGetParameter(final ServerHttpRequest request, final String parameter,
                                          final List<String> parameters) {
        // 第一次请求中 parameters 不包含 parameter:ticket。回直接跳转到下面
        if ("POST".equals(request.getMethod()) && parameters.contains(parameter)) {
            LOGGER.debug("safeGetParameter called on a POST HttpServletRequest for Restricted Parameters.  Cannot complete check safely.  Reverting to standard behavior for this Parameter");
            // request.getParameter("parameterName")，是获得表单(前台页面表单中名称为parameterName)提交的数据。
            // return request.getParameter(parameter);
            // 这里直接返回null
            return null;
        }
        // 第一次请求，request.getQueryString() = null, getQueryString获取请求行后的参数部分
        return request.getQueryParams().isEmpty() || !request.getQueryParams().containsKey(parameter) ? null : request
                .getQueryParams().getFirst(parameter);
    }

    protected static String findMatchingServerName(final ServerHttpRequest request, final String serverName) {
        final String[] serverNames = serverName.split(" ");

        if (serverNames.length == 0 || serverNames.length == 1) {
            return serverName;
        }

        final String host = request.getHeaders().getFirst("Host");
        final String xHost = request.getHeaders().getFirst("X-Forwarded-Host");

        final String comparisonHost;
        comparisonHost = (xHost != null) ? xHost : host;

        if (comparisonHost == null) {
            return serverName;
        }

        for (final String server : serverNames) {
            final String lowerCaseServer = server.toLowerCase();

            if (lowerCaseServer.contains(comparisonHost)) {
                return server;
            }
        }

        return serverNames[0];
    }

    /**
     * Determines if a string is not blank. A string is not blank if it contains
     * at least one non-whitespace character.
     *
     * @param string the string to check.
     * @return true if its not blank, false otherwise.
     */
    public static boolean isNotBlank(final String string) {
        return !isBlank(string);
    }

    /**
     * Determines if a String is blank or not. A String is blank if its empty or
     * if it only contains spaces.
     *
     * @param string the string to check
     * @return true if its blank, false otherwise.
     */
    public static boolean isBlank(final String string) {
        return isEmpty(string) || string.trim().isEmpty();
    }

    /**
     * Determines whether the String is null or of length 0.
     *
     * @param string the string to check
     * @return true if its null or length of 0, false otherwise.
     */
    public static boolean isEmpty(final String string) {
        return string == null || string.isEmpty();
    }

    private static boolean requestIsOnStandardPort(final ServerHttpRequest request) {
        final int serverPort = Objects.requireNonNull(request.getRemoteAddress()).getPort();
        return serverPort == 80 || serverPort == 443;
    }

    public static String constructRedirectUrl(final String casServerLoginUrl, final String serviceParameterName,
                                              final String serviceUrl, final boolean renew, final boolean gateway, final String method) {
        return casServerLoginUrl + (casServerLoginUrl.contains("?") ? "&" : "?") + serviceParameterName + "="
                + urlEncode(serviceUrl) + (renew ? "&renew=true" : "") + (gateway ? "&gateway=true" : "")
                + (method != null ? "&method=" + method : "");
    }

}