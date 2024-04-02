package org.example.gatewaysession.cas.client.validation;

import org.example.gatewaysession.cas.client.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Service tickets validation service for the CAS protocol v3.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class Cas30ServiceTicketValidator extends Cas20ServiceTicketValidator {

    public Cas30ServiceTicketValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    @Override
    protected String getUrlSuffix() {
        return "p3/serviceValidate";
    }

    /**
     * Custom attribute extractor that will account for inlined CAS attributes.  Useful when CAS is acting as
     * as SAML 2 IdP and returns SAML attributes with names that contains namespaces.
     *
     * @param xml the XML to parse.
     * @return - Map of attributes
     */
    @Override
    protected Map<String, Object> extractCustomAttributes(final String xml) {
        final Document document = XmlUtils.newDocument(xml);

        // Check if attributes are inlined.  If not return default super method results
        final NodeList attributeList = document.getElementsByTagName("cas:attribute");
        if (attributeList.getLength() == 0) {
            return super.extractCustomAttributes(xml);
        }

        final HashMap<String, Object> attributes = new HashMap<String, Object>();

        for (int i = 0; i < attributeList.getLength(); i++) {
            final Node casAttributeNode = attributeList.item(i);
            final NamedNodeMap nodeAttributes = casAttributeNode.getAttributes();
            final String name = nodeAttributes.getNamedItem("name").getNodeValue();
            final String value = nodeAttributes.getNamedItem("value").getTextContent();
            final Object mapValue = attributes.get(name);
            if (mapValue != null) {
                if (mapValue instanceof List) {
                    ((List) mapValue).add(value);
                } else {
                    final LinkedList<Object> list = new LinkedList<Object>();
                    list.add(mapValue);
                    list.add(value);
                    attributes.put(name, list);
                }
            } else {
                attributes.put(name, value);
            }
        }
        return attributes;
    }

}
