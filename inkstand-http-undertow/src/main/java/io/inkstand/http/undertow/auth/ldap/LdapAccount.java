package io.inkstand.http.undertow.auth.ldap;

import io.inkstand.security.SimplePrincipal;
import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A user account that is backed by an ldap entry.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class LdapAccount implements Account {

    /**
     * The principal describing the user id
     */
    private final Principal principal;

    /**
     * The name of all roles the user has
     */
    private final Set<String> roles;

    /**
     * The distinguished name of the user's entry in the ldap directory
     */
    private final String dn;

    public LdapAccount(final String userId, final String dn) {
        principal = new SimplePrincipal(userId);
        this.dn = dn;
        roles = new HashSet<>();
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Adds the name of a role of the user to this account.
     *
     * @param roleName
     *            the name of the role
     */
    protected void addRole(final String roleName) {
        roles.add(roleName);
    }

    /**
     * Adds a collection of roles to the account
     * 
     * @param roles
     *            the role names to be added to the account
     */
    protected void addRoles(final Collection<String> roles) {
        this.roles.addAll(roles);
    }

    /**
     * The distinguished name of the user entry in the ldap directory
     *
     * @return the user dn
     */
    public String getDn() {
        return dn;
    }

}
