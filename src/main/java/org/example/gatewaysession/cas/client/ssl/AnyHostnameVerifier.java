package org.example.gatewaysession.cas.client.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Hostname verifier that performs no host name verification for an SSL peer
 * such that all hosts are allowed.
 *
 * @author Marvin Addison
 * @version $Revision$ $Date$
 * @since 3.1.10
 */
public final class AnyHostnameVerifier implements HostnameVerifier {

    /** {@inheritDoc} */
    @Override
    public boolean verify(final String hostname, final SSLSession session) {
        return true;
    }

}
