package org.example.gatewaysession.cas.client.configuration;


import org.example.gatewaysession.cas.client.util.CommonUtils;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;

/**
 * Replicates the original behavior by checking the {@link org.jasig.cas.client.configuration.WebXmlConfigurationStrategyImpl} first, and then
 * the {@link org.jasig.cas.client.configuration.JndiConfigurationStrategyImpl} before using the <code>defaultValue</code>.
 *
 * @author Scott Battaglia
 * @since 3.4.0
 */
public final class LegacyConfigurationStrategyImpl extends BaseConfigurationStrategy {

    private final WebXmlConfigurationStrategyImpl webXmlConfigurationStrategy = new WebXmlConfigurationStrategyImpl();

    private final JndiConfigurationStrategyImpl jndiConfigurationStrategy = new JndiConfigurationStrategyImpl();

    @Override
    public void init(final FilterConfig filterConfig, final Class<? extends Filter> filterClazz) {
        this.webXmlConfigurationStrategy.init(filterConfig, filterClazz);
        this.jndiConfigurationStrategy.init(filterConfig, filterClazz);
    }

    @Override
    protected String get(final ConfigurationKey key) {
        final String value1 = this.webXmlConfigurationStrategy.get(key);

        if (CommonUtils.isNotBlank(value1)) {
            return value1;
        }

        return this.jndiConfigurationStrategy.get(key);
    }
}
