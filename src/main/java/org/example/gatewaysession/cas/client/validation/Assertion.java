package org.example.gatewaysession.cas.client.validation;

import org.example.gatewaysession.cas.client.authentication.AttributePrincipal;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Represents a response to a validation request.
 *
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.1
 */
public interface Assertion extends Serializable {

    /**
     * The date from which the assertion is valid from.
     *
     * @return the valid from date.
     */
    Date getValidFromDate();

    /**
     * The date which the assertion is valid until.
     *
     * @return the valid until date.
     */
    Date getValidUntilDate();

    /**
     * The date the authentication actually occurred on.  If its unable to be determined, it should be set to the current
     * time.
     *
     * @return the authentication date, or the current time if it can't be determined.
     */
    Date getAuthenticationDate();

    /**
     * The key/value pairs associated with this assertion.
     *
     * @return the map of attributes.
     */
    Map<String, Object> getAttributes();

    /**
     * The principal for which this assertion is valid.
     *
     * @return the principal.
     */
    AttributePrincipal getPrincipal();

    /**
     * Determines whether an Assertion is considered usable or not.  A naive implementation may just check the date validity.
     *
     * @return true if its valid, false otherwise.
     * @since 3.3.0 (though in 3.3.0, no one actually calls this)
     */
    boolean isValid();
}
