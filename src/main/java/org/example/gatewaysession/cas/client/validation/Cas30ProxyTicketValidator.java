package org.example.gatewaysession.cas.client.validation;

/**
 * Service and proxy tickets validation service for the CAS protocol v3.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class Cas30ProxyTicketValidator extends Cas20ProxyTicketValidator {

    public Cas30ProxyTicketValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }
    
    @Override
    protected String getUrlSuffix() {
        return "p3/proxyValidate";
    }
}
