/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.http.undertow;

import javax.annotation.Priority;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.inkstand.InkstandRuntimeException;
import io.inkstand.Management;
import io.inkstand.PublicService;
import io.inkstand.config.WebServerConfiguration;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;

/**
 * Provider of an Undertow WebServer instance with a specific deployment configuration. The deployment configuration is
 * injected itself and may be provided by an implementation of {@link UndertowDeploymentProvider}. The {@link Undertow}
 * instance provided by this provider is only configured, but not started.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Singleton
public class UndertowWebServerProvider {

    private static final Logger LOG = LoggerFactory.getLogger(UndertowWebServerProvider.class);

    @Inject
    private WebServerConfiguration config;

    @Inject
    @Management
    private Instance<WebServerConfiguration> mgmtConfig;

    //TODO remove @Default annotation [SCRIB-62]
    @Inject
    @Default
    private Instance<DeploymentInfo> deploymentInfo;

    @Inject
    @Management
    private Instance<DeploymentInfo> mgmtDeployment;

    @Produces
    public Undertow getUndertow() {
        try {
            final Builder builder = Undertow.builder();
            addServices(builder);
            addManagementExtensions(builder);
            return builder.build();
        } catch (final ServletException e) {
            throw new InkstandRuntimeException(e);
        }
    }

    /**
     * Adds the business service deployments that are injected into the provider.
     * @param builder
     *  the builder to create the undertow instance
     * @throws ServletException
     */
    private void addServices(final Builder builder) throws ServletException {

        final ServletContainer container = Servlets.defaultContainer();
        final Iterable<DeploymentInfo> deployments = this.deploymentInfo;
        final PathHandler path = addDeployments(container, deployments);
        final WebServerConfiguration httpConfig = this.config;
        LOG.info("Creating service endpoint {}:{}", httpConfig.getBindAddress(), httpConfig.getPort());
        builder.addHttpListener(httpConfig.getPort(), httpConfig.getBindAddress(), path);
    }

    /**
     * Adds the optional management extensions into the undertow builder configuration. The management extensions
     * have to be injected using the {@link io.inkstand.Management} qualifier.
     * @param builder
     *  the builder to create the undertow instance.
     * @throws ServletException
     */
    private void addManagementExtensions(final Builder builder) throws ServletException {

        //mgmt Deployment is completely optional and will only be activated, if there is a mgmt config
        if (!mgmtConfig.isUnsatisfied() && !mgmtDeployment.isUnsatisfied()) {
            final WebServerConfiguration mCfg = mgmtConfig.get();
            LOG.info("Creating management endpoint {}:{}", mCfg.getBindAddress(), mCfg.getPort());
            final ServletContainer mContainer = Servlets.newContainer();
            final HttpHandler root = addDeployments(mContainer, this.mgmtDeployment);
            builder.addHttpListener(mCfg.getPort(), mCfg.getBindAddress(), root);
        }
    }

    /**
     * Adds deployments to a servlet container. Each deployment is registered at a separate context path provided by
     * their {@link io.undertow.servlet.api.DeploymentInfo}
     * @param container
     *  the container to which the the deployments should be added
     * @param deployments
     *  the deployments to be added
     * @return
     *  a path handler to the deployments. Multiple deployments have to be distinguished by their context paths.
     * @throws ServletException
     */
    private PathHandler addDeployments(final ServletContainer container, final Iterable<DeploymentInfo> deployments)
            throws ServletException {

        final PathHandler path = new PathHandler();
        for (DeploymentInfo di : deployments) {
            final DeploymentManager dm = container.addDeployment(di);
            LOG.info("Deploying service {}", di.getContextPath());
            dm.deploy();
            path.addPrefixPath(di.getContextPath(), dm.start());
        }
        return path;
    }

    /**
     * Stereotyped version of the default UndertowWebServerProvider that can be activated using the {@link
     * PublicService} stereotype in beans.xml.
     */
    @Priority(1)
    @PublicService
    @Singleton
    private static class PublicUndertowWebServiceProvider extends UndertowWebServerProvider {

    }
}
