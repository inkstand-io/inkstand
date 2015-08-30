/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.http.undertow.auth.ldap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.inkstand.InkstandRuntimeException;
import io.inkstand.security.InvalidCredentialsException;
import io.inkstand.security.LdapAuthConfiguration;
import io.inkstand.security.UserNotFoundException;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;

/**
 * Undertow {@link IdentityManager} that verifies user ids by looking them up in an LDAP directory.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class LdapIdentityManager implements IdentityManager {

    private static final Logger LOG = LoggerFactory.getLogger(LdapIdentityManager.class);

    /**
     * Configuration for the ldap connection and the lookups.
     */
    @Inject
    private LdapAuthConfiguration ldapConfig;

    /**
     * The connection handle to the LDAP server.
     */
    private LdapConnection connection;

    @PostConstruct
    public void connect() {

        LOG.debug("Connecting to LDAP server ldap://{}:{}", this.ldapConfig.getHostname(), this.ldapConfig.getPort());
        this.connection = new LdapNetworkConnection(this.ldapConfig.getHostname(), this.ldapConfig.getPort());
        try {
            this.connection.connect();
        } catch (LdapException e) {
            throw new InkstandRuntimeException("Could not connect to LDAP server at "
                                                       + this.ldapConfig.getHostname()
                                                       + ":"
                                                       + this.ldapConfig.getPort(), e);
        }
    }

    @PreDestroy
    public void disconnect() {

        if (this.connection.isConnected()) {
            try {
                this.connection.close();
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
    public Account verify(final String userId, final Credential credential) {

        this.bind();
        try {
            final EntryCursor result = this.connection.search(this.ldapConfig.getUserContextDn(),
                                                             this. getUserFilter(userId),
                                                              this.getSearchScope());
            if (result.next()) {
                final Entry user = result.get();
                return this.createUserAccount(user, credential, userId);
            }
            throw new UserNotFoundException(userId);
        } catch (final LdapException | CursorException e) {
            throw new InkstandRuntimeException(e);
        }
    }

    @Override
    public Account verify(final Credential credential) {

        throw new UnsupportedOperationException("verify with credentials not supported");
    }

    /**
     * Creates an {@link LdapAccount} for the given LDAP entry and credentials. The account is populated with the roles
     * retrieved from the LDAP server.
     *
     * @param user
     *         the user entry from the LDAP service
     * @param credential
     *         the credentials used to perform the login
     * @param userId
     *         the id of the user
     *
     * @return the created user account
     *
     * @throws LdapException
     *         if the lookup for roles or the authentication fails
     * @throws CursorException
     *         if the roles could not be retrieved for the user
     */
    private LdapAccount createUserAccount(final Entry user, final Credential credential, final String userId)
            throws CursorException, LdapException {

        LOG.debug("User {} found, collecting user groups", userId);

        final Set<String> roles = this.getRoles(userId, user.getDn().toString());
        LOG.debug("User {} has roles {}", userId, roles);

        LOG.debug("Authenticating user {}", userId);
        final char[] password = ((PasswordCredential) credential).getPassword();
        try {
            this.connection.bind(user.getDn(), String.valueOf(password));
        } catch (LdapException e) {
            throw new InvalidCredentialsException(userId, e);
        }
        LOG.debug("User {} authenticated", userId);

        final LdapAccount account = new LdapAccount(userId, user.getDn().toString());
        account.addRoles(roles);
        return account;
    }

    /**
     * Performs a bind with the configured bind user on the ldap connection.
     */
    private void bind() {

        try {
            LOG.debug("binding user {}", this.ldapConfig.getBindDn());
            this.connection.bind(this.ldapConfig.getBindDn(), this.ldapConfig.getBindCredentials());
        } catch (final LdapException e) {
            throw new InkstandRuntimeException("Ldap authentication failed", e);
        }
    }

    /**
     * Retrieves all role names for the current user.
     *
     * @param uid
     *         the userID of the user to retrieve the roles for
     * @param userDn
     *         the distringuished name of the user entry to retrieve the roles for
     *
     * @return a set of role names
     *
     * @throws LdapException
     *         if the lookup could not be performed for any reason
     * @throws CursorException
     *         if the result of the search could not be processed
     */
    private Set<String> getRoles(final String uid, final String userDn) throws LdapException, CursorException {

        final EntryCursor result = this.connection.search(this.ldapConfig.getRoleContextDn(),
                                                          this.getRoleFilter(uid, userDn),
                                                          this.getSearchScope(),
                                                          this.ldapConfig.getRoleNameAttribute());
        final Set<String> roles = new HashSet<>();
        while (result.next()) {
            roles.add(result.get().get(this.ldapConfig.getRoleNameAttribute()).getString());
        }

        return roles;
    }

    /**
     * Retrieves the filter string where the placeholder for the user id is replaced with the given id.
     *
     * @param userId
     *         the unique identifier of a user
     *
     * @return a filter expression for filtering entries for users
     */
    private String getUserFilter(final String userId) {

        return this.ldapConfig.getUserFilter().replaceAll("\\{0\\}", userId);
    }

    /**
     * Retrieves the filter string with the placeholders for the user are replaced with the user id and/or the dn of the
     * users. The following placeholders are supported <ul> <li>{0} is replaced with the userId</li> <li>{1} is replaced
     * with the userDN</li> </ul>
     *
     * @param userId
     *         the unique identifier of a user
     * @param userDn
     *         the dn of the user to perform the role lookup
     *
     * @return a filter expression for filtering entries for users
     */
    private String getRoleFilter(final String userId, final String userDn) {

        return this.ldapConfig.getRoleFilter().replaceAll("\\{0\\}", userId).replaceAll("\\{1\\}", userDn);
    }

    /**
     * The search scope for the search operations from the configuration.
     *
     * @return the search scope
     */
    private SearchScope getSearchScope() {

        return SearchScope.getSearchScope(this.ldapConfig.getSearchScope().getValue());
    }
}
