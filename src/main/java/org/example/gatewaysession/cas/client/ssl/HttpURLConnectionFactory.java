package org.example.gatewaysession.cas.client.ssl;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * A factory to prepare and configure {@link java.net.URLConnection} instances. 
 *
 * @author Misagh Moayyed
 * @since 3.3
 */
public interface HttpURLConnectionFactory extends Serializable {

    /**
     * Receives a {@link URLConnection} instance typically as a result of a {@link URL}
     * opening a connection to a remote resource. The received url connection is then
     * configured and prepared appropriately depending on its type and is then returned to the caller
     * to accommodate method chaining.
     *  
     * @param url The url connection that needs to be configured
     * @return The configured {@link HttpURLConnection} instance
     * 
     * @see {@link HttpsURLConnectionFactory}
     */
    HttpURLConnection buildHttpURLConnection(final URLConnection url);
}
