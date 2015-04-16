package io.inkstand.security;

import io.inkstand.config.LdapConfiguration;

/**
 * Configuration interface for an LDAP Server to be used for authentication purposes.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public interface LdapAuthConfiguration extends LdapConfiguration {

    /**
     * LDAP Search Scopes Search scope
     *
     * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
     */
    public static enum SearchScope {
        // TODO potential candidate for inkstand-ldap
        BASE(0, "base"),
        ONE_LEVEL(1, "one"),
        SUBTREE(2, "sub"), ;

        private final String name;
        private final int value;

        private SearchScope(final int value, final String name) {
            this.value = value;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

    }

    /**
     * The scope for user or group searches
     *
     * @return
     */
    public abstract SearchScope getSearchScope();

    /**
     * The attribute that contains the role name. The role is used as role def in web applications.<br>
     * Example: <code>cn</code>
     *
     * @return the attribute name for the role name.
     */
    public abstract String getRoleNameAttribute();

    /**
     * The filter for finding roles inside the role context and the search scope.<br>
     * Example:<code>(uniqueMember={1})</code>
     *
     * @return the filter to search for roles
     */
    public abstract String getRoleFilter();

    /**
     * The DN containing the roles. <br>
     * Example: <code>ou=groups,ou=system</code>
     *
     * @return the role context dn
     */
    public abstract String getRoleContextDn();

    /**
     * The filter for finding users inside the user context and the search scope. For example:<br>
     * <code>ou=users,ou=system</code>
     *
     * @return the filter to search for users
     */
    public abstract String getUserFilter();

    /**
     * The dn of the context to search for users. <br>
     * Example:<code>ou=users,ou=system</code>
     *
     * @return the dn of the user context
     */
    public abstract String getUserContextDn();

}
