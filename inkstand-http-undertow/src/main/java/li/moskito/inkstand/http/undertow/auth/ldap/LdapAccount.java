package li.moskito.inkstand.http.undertow.auth.ldap;

import io.undertow.security.idm.Account;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import li.moskito.inkstand.security.SimplePrincipal;

/**
 * A user account that is backed by an ldap entry.
 * 
 * @author Gerald Muecke, gerald@moskito.li
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
     * The distinguished name of the user entry in the ldap directory
     *
     * @return the user dn
     */
    public String getDn() {
        return dn;
    }

}