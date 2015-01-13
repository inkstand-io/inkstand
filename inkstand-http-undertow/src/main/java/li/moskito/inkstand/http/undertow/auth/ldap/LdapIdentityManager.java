package li.moskito.inkstand.http.undertow.auth.ldap;

import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import li.moskito.inkstand.InkstandRuntimeException;
import li.moskito.inkstand.security.LdapAuthConfiguration;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Undertow {@link IdentityManager} that verifies user ids by looking them up in an LDAP directory.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
public class LdapIdentityManager implements IdentityManager {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(LdapIdentityManager.class);

    /**
     * Configuration for the ldap connection and the lookups
     */
    @Inject
    private LdapAuthConfiguration ldapConfig;

    /**
     * The connection handle to the LDAP server.
     */
    private LdapConnection connection;

    @PostConstruct
    public void connect() {
        LOG.debug("Connecting to LDAP server ldap://{}:{}", ldapConfig.getHostname(), ldapConfig.getPort());
        connection = new LdapNetworkConnection(ldapConfig.getHostname(), ldapConfig.getPort());
    }

    @PreDestroy
    public void disconnect() {
        if (connection.isConnected()) {
            try {
                connection.close();
            } catch (final IOException e) {
                LOG.warn("Closing connection failed", e);
            }
        }
    }

    @Override
    public Account verify(final Account account) {
        return account;
    }

    @Override
    public Account verify(final String id, final Credential credential) {
        bind();
        try {
            final EntryCursor result = connection.search(ldapConfig.getUserContextDn(), getUserFilter(id),
                    getSearchScope());
            if (result.next()) {
                final Entry user = result.get();
                return createUserAccout(user, credential, id);
            }
            // TODO replace with authentication exception
            throw new InkstandRuntimeException("No user with id " + id + " found");
        } catch (final LdapException | CursorException e) {
            throw new InkstandRuntimeException(e);
        } finally {
            unbind();
        }
    }

    private LdapAccount createUserAccout(final Entry user, final Credential credential, final String id)
            throws LdapException, CursorException {

        LOG.debug("User {} found, collecting user groups", id);
        final Set<String> roles = getRoles(id, user.getDn().toString());
        LOG.debug("User {} has roles {}", id, roles);
        LOG.debug("Authenticating user {}", id);
        final char[] password = ((PasswordCredential) credential).getPassword();
        connection.bind(user.getDn(), String.valueOf(password));
        LOG.debug("User {} authenticated", id);
        final LdapAccount account = new LdapAccount(id, user.getDn().toString());
        for (final String role : roles) {
            account.addRole(role);
        }
        return account;
    }

    @Override
    public Account verify(final Credential credential) {
        throw new UnsupportedOperationException("verify with credentials not supported");
    }

    /**
     * Binds the
     */
    private void bind() {
        try {
            LOG.debug("binding user {}", ldapConfig.getBindDn());
            connection.bind(ldapConfig.getBindDn(), ldapConfig.getBindCredentials());
        } catch (final LdapException e) {
            throw new InkstandRuntimeException("Ldap authentication failed", e);
        }
    }

    private void unbind() {
        try {
            connection.unBind();
        } catch (final LdapException e) {
            throw new InkstandRuntimeException("Ldap unbind failed", e);
        }
    }

    private Set<String> getRoles(final String uid, final String dn) throws LdapException, CursorException {
        bind();
        final EntryCursor result = connection.search(ldapConfig.getRoleContextDn(), getRoleFilter(uid, dn),
                getSearchScope(), ldapConfig.getRoleNameAttribute());
        final Set<String> roles = new HashSet<>();
        while (result.next()) {
            roles.add(result.get().get(ldapConfig.getRoleNameAttribute()).getString());
        }

        return roles;
    }

    /**
     * Retrieves the filter string where the placeholder for the user id is replaced with the given id.
     *
     * @param userId
     * @return
     */
    private String getUserFilter(final String userId) {
        return ldapConfig.getUserFilter().replaceAll("\\{0\\}", userId);
    }

    private String getRoleFilter(final String userId, final String userDn) {
        return ldapConfig.getRoleFilter().replaceAll("\\{0\\}", userId).replaceAll("\\{1\\}", userDn);
    }

    private SearchScope getSearchScope() {
        return SearchScope.getSearchScope(ldapConfig.getSearchScope().getValue());
    }
}
