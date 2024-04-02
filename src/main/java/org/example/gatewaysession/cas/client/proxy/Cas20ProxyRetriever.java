package org.example.gatewaysession.cas.client.proxy;

import java.net.URL;
import java.net.URLEncoder;

import org.example.gatewaysession.cas.client.ssl.HttpURLConnectionFactory;
import org.example.gatewaysession.cas.client.util.CommonUtils;
import org.example.gatewaysession.cas.client.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a ProxyRetriever that follows the CAS 2.0 specification.
 * For more information on the CAS 2.0 specification, please see the <a
 * href="http://www.jasig.org/cas/protocol">specification
 * document</a>.
 * <p/>
 * In general, this class will make a call to the CAS server with some specified
 * parameters and receive an XML response to parse.
 *
 * @author Scott Battaglia
 * @since 3.0
 */
public final class Cas20ProxyRetriever implements ProxyRetriever {

    /** Unique Id for serialization. */
    private static final long serialVersionUID = 560409469568911792L;

    private static final Logger logger = LoggerFactory.getLogger(Cas20ProxyRetriever.class);

    /**
     * Url to CAS server.
     */
    private final String casServerUrl;

    private final String encoding;

    /** Url connection factory to use when communicating with the server **/
    private final HttpURLConnectionFactory urlConnectionFactory;

    @Deprecated
    public Cas20ProxyRetriever(final String casServerUrl, final String encoding) {
        this(casServerUrl, encoding, null);
    }

    /**
     * Main Constructor.
     *
     * @param casServerUrl the URL to the CAS server (i.e. http://localhost/cas/)
     * @param encoding the encoding to use.
     * @param urlFactory url connection factory use when retrieving proxy responses from the server
     */
    public Cas20ProxyRetriever(final String casServerUrl, final String encoding,
            final HttpURLConnectionFactory urlFactory) {
        CommonUtils.assertNotNull(casServerUrl, "casServerUrl cannot be null.");
        this.casServerUrl = casServerUrl;
        this.encoding = encoding;
        this.urlConnectionFactory = urlFactory;
    }

    @Override
    public String getProxyTicketIdFor(final String proxyGrantingTicketId, final String targetService) {
        CommonUtils.assertNotNull(proxyGrantingTicketId, "proxyGrantingTicketId cannot be null.");
        CommonUtils.assertNotNull(targetService, "targetService cannot be null.");

        final URL url = constructUrl(proxyGrantingTicketId, targetService);
        final String response;

        if (this.urlConnectionFactory != null) {
            response = CommonUtils.getResponseFromServer(url, this.urlConnectionFactory, this.encoding);
        } else {
            response = CommonUtils.getResponseFromServer(url, this.encoding);
        }
        final String error = XmlUtils.getTextForElement(response, "proxyFailure");

        if (CommonUtils.isNotEmpty(error)) {
            logger.debug(error);
            return null;
        }

        final String ticket = XmlUtils.getTextForElement(response, "proxyTicket");
        logger.debug("Got proxy ticket {}", ticket);
        return ticket;
    }

    private URL constructUrl(final String proxyGrantingTicketId, final String targetService) {
        try {
            return new URL(this.casServerUrl + (this.casServerUrl.endsWith("/") ? "" : "/") + "proxy" + "?pgt="
                    + proxyGrantingTicketId + "&targetService=" + URLEncoder.encode(targetService, "UTF-8"));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
