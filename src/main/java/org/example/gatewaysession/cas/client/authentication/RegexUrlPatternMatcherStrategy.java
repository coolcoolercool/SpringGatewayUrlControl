package org.example.gatewaysession.cas.client.authentication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A pattern matcher that looks inside the url to find the pattern, that
 * is assumed to have been specified via regular expressions syntax.
 * The match behavior is based on {@link Matcher#find()}:
 * Attempts to find the next subsequence of the input sequence that matches
 * the pattern. This method starts at the beginning of this matcher's region, or, if
 * a previous invocation of the method was successful and the matcher has
 * not since been reset, at the first character not matched by the previous
 * match.
 *
 * @author Misagh Moayyed
 * @since 3.3.1
 */
public final class RegexUrlPatternMatcherStrategy implements UrlPatternMatcherStrategy {

    private Pattern pattern;

    public RegexUrlPatternMatcherStrategy() {
    }

    public RegexUrlPatternMatcherStrategy(final String pattern) {
        this.setPattern(pattern);
    }

    @Override
    public boolean matches(final String url) {
        return this.pattern.matcher(url).find();
    }

    @Override
    public void setPattern(final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
}
