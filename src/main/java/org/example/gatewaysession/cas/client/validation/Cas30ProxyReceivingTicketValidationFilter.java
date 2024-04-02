package org.example.gatewaysession.cas.client.validation;


import org.example.gatewaysession.cas.client.Protocol;

/**
 * Creates either a Cas30ProxyTicketValidator or a Cas30ServiceTicketValidator depending on whether any of the
 * proxy parameters are set.
 * <p/>
 * This filter can also pass additional parameters to the ticket validator.  Any init parameter not included in the
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class Cas30ProxyReceivingTicketValidationFilter extends Cas20ProxyReceivingTicketValidationFilter {

    public Cas30ProxyReceivingTicketValidationFilter() {
        super(Protocol.CAS3);
        this.defaultServiceTicketValidatorClass = Cas30ServiceTicketValidator.class;
        this.defaultProxyTicketValidatorClass = Cas30ProxyTicketValidator.class;
    }
}
