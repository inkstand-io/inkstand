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

import javax.inject.Inject;

import io.inkstand.security.LdapAuthConfiguration;

import org.apache.deltaspike.core.api.config.ConfigProperty;

public class DefaultLdapConfiguration implements LdapAuthConfiguration {
    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.port", defaultValue = "389")
    private int port;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.host", defaultValue = "localhost")
    private String hostname;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.bind.dn")
    private String bindDn;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.bind.credentials")
    private String bindCredentials;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.user.context.dn")
    private String userContextDn;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.user.filter")
    private String userFilter;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.role.context.dn")
    private String roleContextDn;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.role.filter")
    private String roleFilter;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.role.nameAttribute")
    private String roleNameAttribute;

    @Inject
    @ConfigProperty(name = "inkstand.auth.ldap.searchScope", defaultValue = "SUBTREE")
    private String searchScope;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getBindDn() {
        return bindDn;
    }

    @Override
    public String getBindCredentials() {
        return bindCredentials;
    }

    @Override
    public String getUserContextDn() {
        return userContextDn;
    }

    @Override
    public String getUserFilter() {
        return userFilter;
    }

    @Override
    public String getRoleContextDn() {
        return roleContextDn;
    }

    @Override
    public String getRoleFilter() {
        return roleFilter;
    }

    @Override
    public String getRoleNameAttribute() {
        return roleNameAttribute;
    }

    @Override
    public SearchScope getSearchScope() {
        return SearchScope.valueOf(searchScope);
    }

}
