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

package io.inkstand.http;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.deltaspike.core.api.config.ConfigProperty;

import io.inkstand.config.ResourceSecurityConfiguration;

/**
 * Configuration implementation providing injectable properties using Deltaspike's configuration mechanism. The
 * configuration can be populated by defining the following system properties:
 * <ul>
 *     <li>{@code inkstand.http.auth.realm} -  the name of the security realm. Default is 'Inkstand'.</li>
 *     <li>{@code inkstand.http.auth.method} - the authentication method to use in upper-case. Default is 'BASIC'</li>
 *     <li>{@code inkstand.http.auth.securityRoles} - the names of the security roles in this realm, separated by
 *     comma ','. Default is 'Users'</li>
 *     <li>{@code inkstand.http.auth.allowedRoles} - the names of the security roles that are allowed to access
 *     the protected resources. Must be in the list of security roles. Separated by comma ','. Default is 'Users'</li>
 *     <li>{@code inkstand.http.auth.protectedResources} - the URL patterns defining which resources are protected by
 *     this configuration. If multiple patterns are specified, they must be separated by semi-colon ';'. Default is
 *     '/*'.
 *     </li>
 * </ul>
 * Created by Gerald on 17.06.2015.
 */
public class DefaultResourceSecurityConfiguration implements ResourceSecurityConfiguration {

    @Inject
    @ConfigProperty(name = "inkstand.http.auth.realm",
                    defaultValue = "Inkstand")
    private String realm;

    @Inject
    @ConfigProperty(name = "inkstand.http.auth.method",
                    defaultValue = "BASIC")
    private String authMethod;

    private Set<String> securityRoles = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Users")));

    private Set<String> allowedRoles = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Users")));

    private Set<String> protectedResources = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("/*")));

    @Override
    public String getRealm() {

        return realm;
    }

    @Override
    public String getAuthenticationMethod() {

        return authMethod;
    }

    @Override
    public Set<String> getSecurityRoles() {

        return securityRoles;
    }

    @Override
    public Set<String> getAllowedRoles() {

        return allowedRoles;
    }

    @Override
    public Set<String> getProtectedResources() {

        return protectedResources;
    }

    @Inject
    public void setSecurityRoles(
            @ConfigProperty(name = "inkstand.http.auth.securityRoles",
                            defaultValue = "Users")
            String securityRoles) {
        this.securityRoles = asSet(securityRoles, ',');
    }

    @Inject
    public void setAllowedRoles(
            @ConfigProperty(name = "inkstand.http.auth.allowedRoles",
                            defaultValue = "Users")
            String allowedRoles) {
        this.allowedRoles = asSet(allowedRoles, ',');
    }

    @Inject
    public void setProtectedResourcs(
            @ConfigProperty(name = "inkstand.http.auth.protectedResources",
                            defaultValue = "/*")
            String protectedResources) {

        this.protectedResources = asSet(protectedResources, ';');
    }

    private Set<String> asSet(String inputString, char separator) {

        Set<String> result = new HashSet<>();
        StringTokenizer tok = new StringTokenizer(inputString, String.valueOf(separator));
        while(tok.hasMoreTokens()){
            result.add(tok.nextToken());
        }
        return result;
    }

}
