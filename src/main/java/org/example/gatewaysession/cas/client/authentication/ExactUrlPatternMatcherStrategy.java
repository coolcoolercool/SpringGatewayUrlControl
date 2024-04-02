package org.example.gatewaysession.cas.client.authentication;

/**
 * A pattern matcher that produces a successful match if the pattern
 * specified matches the given url exactly and equally.
 * 
 * @author Misagh Moayyed
 * @since 3.3.1
 */
public final class ExactUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private String pattern;

    public ExactUrlPatternMatcherStrategy() {}

    public ExactUrlPatternMatcherStrategy(final String pattern) {
        this.setPattern(pattern);
    }

    @Override
    public boolean matches(final String url) {
        return url.equals(this.pattern);
    }

    @Override
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

}
