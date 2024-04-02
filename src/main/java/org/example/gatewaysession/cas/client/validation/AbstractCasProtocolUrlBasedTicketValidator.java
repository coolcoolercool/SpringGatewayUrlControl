package org.example.gatewaysession.cas.client.validation;

import org.example.gatewaysession.cas.client.util.CommonUtils;

import java.net.URL;

/**
 * Abstract class that knows the protocol for validating a CAS ticket.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public abstract class AbstractCasProtocolUrlBasedTicketValidator extends AbstractUrlBasedTicketValidator {

    protected AbstractCasProtocolUrlBasedTicketValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    /**
     * Retrieves the response from the server by opening a connection and merely reading the response.
     */
    @Override
    protected final String retrieveResponseFromServer(final URL validationUrl, final String ticket) {
        return CommonUtils.getResponseFromServer(validationUrl, getURLConnectionFactory(), getEncoding());
    }
}
