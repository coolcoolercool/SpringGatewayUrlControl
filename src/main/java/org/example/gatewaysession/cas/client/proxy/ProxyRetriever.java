package org.example.gatewaysession.cas.client.proxy;

import java.io.Serializable;

/**
 * Interface to abstract the retrieval of a proxy ticket to make the
 * implementation a black box to the client.
 *
 * @author Scott Battaglia
 * @since 3.0
 */
public interface ProxyRetriever extends Serializable {

    /**
     * Retrieves a proxy ticket for a specific targetService.
     *
     * @param proxyGrantingTicketId the ProxyGrantingTicketId
     * @param targetService         the service we want to proxy.
     * @return the ProxyTicket Id if Granted, null otherwise.
     */
    String getProxyTicketIdFor(String proxyGrantingTicketId, String targetService);
}
