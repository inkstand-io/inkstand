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

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.inkstand.security.SimplePrincipal;
import io.undertow.security.idm.Account;

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
    private final String userDn;

    public LdapAccount(final String userId, final String userDn) {
        this.principal = new SimplePrincipal(userId);
        this.userDn = userDn;
        this.roles = new HashSet<>();
    }

    @Override
    public Principal getPrincipal() {
        return this.principal;
    }

    @Override
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    /**
     * Adds the name of a role of the user to this account.
     *
     * @param roleName
     *            the name of the role
     */
    protected void addRole(final String roleName) {
        this.roles.add(roleName);
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
     * @return the user userDn
     */
    public String getUserDn() {
        return this.userDn;
    }

}
