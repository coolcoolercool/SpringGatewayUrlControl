package org.example.gatewaysession.cas.client.authentication;

/**
 * A pattern matcher that looks inside the url to find the exact pattern specified.
 * 
 * @author Misagh Moayyed
 * @since 3.3.1
 */
public final class ContainsPatternUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private String pattern;
    
    @Override
    public boolean matches(final String url) {
        return url.contains(this.pattern);
    }

    @Override
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }
}
