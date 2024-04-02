package org.example.gatewaysession.cas.client.validation;

import org.example.gatewaysession.cas.client.util.CommonUtils;

import java.beans.PropertyEditorSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Convert a String-formatted list of acceptable proxies to an array.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 * 
 */
public final class ProxyListEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        final List<String[]> proxyChains = new ArrayList<String[]>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (CommonUtils.isNotBlank(line)) {
                    proxyChains.add(line.trim().split(" "));
                }
            }
        } catch (final IOException e) {
            // ignore this
        } finally {
            try {
                reader.close();
            } catch (final IOException e) {
                // nothing to do
            }
        }

        setValue(new ProxyList(proxyChains));
    }
}
