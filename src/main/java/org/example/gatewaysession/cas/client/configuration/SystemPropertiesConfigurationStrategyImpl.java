package org.example.gatewaysession.cas.client.configuration;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;

/**
 * Load all configuration from system properties.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class SystemPropertiesConfigurationStrategyImpl extends BaseConfigurationStrategy {

    @Override
    public void init(final FilterConfig filterConfig, final Class<? extends Filter> filterClazz) {
    }

    @Override
    protected String get(final ConfigurationKey configurationKey) {
        return System.getProperty(configurationKey.getName());
    }
}
