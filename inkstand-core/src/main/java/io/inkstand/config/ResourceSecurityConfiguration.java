package io.inkstand.config;

import java.util.Set;

/**
 * Configuration for securing a web resource.
 * Created by Gerald on 17.06.2015.
 */
public interface ResourceSecurityConfiguration {

    /**
     * The name of the security realm
     * @return
     */
    String getRealm();

    /**
     * The authentication method to use
     * @return
     */
    String getAuthenticationMethod();

    /**
     * Set of role names for the security realm
     * @return
     */
    Set<String> getSecurityRoles();

    /**
     * Sets of role names that are allowed to access the resources
     * @return
     */
    Set<String> getAllowedRoles();

    /**
     * A set of URL patterns denoting the protected resource.
     * @return
     *  set of string patterns
     */
    Set<String> getProtectedResources();

}
