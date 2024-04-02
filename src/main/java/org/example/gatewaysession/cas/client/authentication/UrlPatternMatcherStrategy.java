package org.example.gatewaysession.cas.client.authentication;

/**
 * Defines an abstraction by which request urls can be matches against a given pattern.
 * New instances for all extensions for this strategy interface will be created per
 * each request. The client will ultimately invoke the {@link #matches(String)} method
 * having already applied and set the pattern via the {@link #setPattern(String)} method.
 * The pattern itself will be retrieved via the client configuration.
 * @author Misagh Moayyed
 * @since 3.3.1
 */
public interface UrlPatternMatcherStrategy {
    /**
     * Execute the match between the given pattern and the url
     * @param url the request url typically with query strings included
     * @return true if match is successful
     */
    boolean matches(String url);
    
    /**
     * The pattern against which the url is compared
     * @param pattern
     */
    void setPattern(String pattern);
}
