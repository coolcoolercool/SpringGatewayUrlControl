package org.example.gatewaysession.cas.client.authentication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A pattern matcher that looks inside the url to find the pattern, that
 * is assumed to have been specified via regular expressions syntax.
 * The match behavior is based on {@link Matcher#matches()}:
 * Attempts to match the entire region against the pattern.
 *
 * @author Misagh Moayyed
 * @since 3.5
 */
public final class EntireRegionRegexUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private Pattern pattern;

    public EntireRegionRegexUrlPatternMatcherStrategy() {
    }

    public EntireRegionRegexUrlPatternMatcherStrategy(final String pattern) {
        this.setPattern(pattern);
    }

    @Override
    public boolean matches(final String url) {
        return this.pattern.matcher(url).matches();
    }

    @Override
    public void setPattern(final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
}
