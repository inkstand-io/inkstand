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

package io.inkstand.deployment.resteasy;

import javax.annotation.Priority;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Set;

import io.inkstand.ProtectedService;
import io.inkstand.config.ResourceSecurityConfiguration;
import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.LoginConfig;
import io.undertow.servlet.api.SecurityConstraint;

/**
 * Provider for producing an {@link DeploymentInfo} for an {@link Undertow} web server that provides Jax-RS support
 * based on RestEasy. The deployment allows to host Jax-RS resources and providers in a webserver.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Singleton
@ProtectedService
@Priority(0)
public class BasicSecurityResteasyDeploymentProvider extends DefaultResteasyDeploymentProvider {

    @Inject
    private ResourceSecurityConfiguration secConfig;

    @Override
    @Produces
    public DeploymentInfo getDeployment() {

        final DeploymentInfo di = super.getDeployment();
        di.setLoginConfig(createLoginConfig(secConfig.getRealm(), secConfig.getAuthenticationMethod()));
        di.addSecurityRoles(secConfig.getSecurityRoles());
        di.addSecurityConstraint(createSecurityConstraint(secConfig.getAllowedRoles(),
                                                          secConfig.getProtectedResources()));
        return di;

    }

    /**
     * Creates a login configuration using the specified realm and authentication method.
     * @param realm
     *  the realm of the login configuration
     * @param authenticationMethod
     *  the authentication method used for authenticating users for this realm
     * @return
     *  the login configuration for a {@link DeploymentInfo}
     */
    private LoginConfig createLoginConfig(final String realm, final String authenticationMethod) {

        return Servlets.loginConfig(realm).addFirstAuthMethod(authenticationMethod);
    }

    /**
     * Creates a security constraint that maps a set of roles that should have access to a set of resources
     * defined by a set of URL patterns.
     * @param allowedRoles
     *  the role names that are allowed
     * @param protectedResources
     *  the URL patterns (like /*) that define the resource whose access is constraint
     * @return
     *  the security constraint for the resources
     */
    private SecurityConstraint createSecurityConstraint(final Set<String> allowedRoles,
                                                        final Set<String> protectedResources) {

        return Servlets.securityConstraint()
                       .addRolesAllowed(allowedRoles)
                       .addWebResourceCollection(Servlets.webResourceCollection().addUrlPatterns(protectedResources));
    }
}
