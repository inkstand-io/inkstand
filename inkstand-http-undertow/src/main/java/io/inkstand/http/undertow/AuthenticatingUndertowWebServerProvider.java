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

package io.inkstand.http.undertow;

import javax.annotation.Priority;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.inkstand.InkstandRuntimeException;
import io.inkstand.ProtectedService;
import io.inkstand.config.ResourceSecurityConfiguration;
import io.inkstand.config.WebServerConfiguration;
import io.undertow.Undertow;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

/**
 * Provider of an Undertow WebServer instance with a specific deployment configuration. The deployment configuration is
 * injected itself and may be provided by an implementation of {@link UndertowDeploymentProvider}. The {@link Undertow}
 * instance provided by this provider is only configured, but not started.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Singleton
@Priority(0)
@ProtectedService
public class AuthenticatingUndertowWebServerProvider {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(UndertowWebServerProvider.class);

    @Inject
    private WebServerConfiguration config;

    @Inject
    private ResourceSecurityConfiguration securityConfig;

    @Inject
    private DeploymentInfo deploymentInfo;

    @Inject
    private IdentityManager identityManager;

    /**
     * Creates an {@link Undertow} instance that has an {@link IdentityManager} associated to secure the resources
     * served by the undertow http server.
     *
     * @return a secured undertow instance
     */
    @Produces
    public Undertow getSecuredUndertow() {

        this.deploymentInfo.setIdentityManager(identityManager);

        final DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(this.deploymentInfo);
        deploymentManager.deploy();

        try {
            LOG.info("Creating service endpoint {}:{}/{} for {}",
                     this.config.getBindAddress(),
                     this.config.getPort(),
                     this.deploymentInfo.getContextPath(),
                     this.deploymentInfo.getDeploymentName());
            return Undertow.builder()
                           .addHttpListener(this.config.getPort(), this.config.getBindAddress())
                           .setHandler(addSecurity(deploymentManager.start()))
                           .build();
        } catch (final ServletException e) {
            throw new InkstandRuntimeException(e);
        }
    }

    /**
     * Adds the security handlers to the specified http handler.
     *
     * @param toWrap
     *         the {@link HttpHandler} to be wrapped into security handlers
     *
     * @return a {@link HttpHandler} that provides security to the served resources.
     */
    HttpHandler addSecurity(final HttpHandler toWrap) {

        final List<AuthenticationMechanism> mechanisms = getAuthenticationMechanisms();

        HttpHandler handler = toWrap;
        handler = new AuthenticationCallHandler(handler);
        handler = new AuthenticationConstraintHandler(handler);
        handler = new AuthenticationMechanismsHandler(handler, mechanisms);
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler);
        return handler;
    }

    private List<AuthenticationMechanism> getAuthenticationMechanisms() {

        final List<AuthenticationMechanism> mechanisms = new ArrayList<>();
        switch (this.securityConfig.getAuthenticationMethod()) {
            case "BASIC":
                mechanisms.add(new BasicAuthenticationMechanism(this.securityConfig.getRealm()));
                break;
            default:
                throw new IllegalArgumentException(this.securityConfig.getAuthenticationMethod()
                                                           + " is no supported security mechanism");
        }
        return mechanisms;
    }
}
