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

    @Inject
    private DeploymentInfo deploymentInfo;

    @Inject
    @Management
    private Instance<DeploymentInfo> mgmtDeployment;

    @Produces
    public Undertow getUndertow() {

        final WebServerConfiguration httpConfig = this.getConfig();

        try {

            final DeploymentInfo deployInfo = this.getDeploymentInfo();
            final DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(deployInfo);
            deploymentManager.deploy();
            LOG.info("Creating service endpoint {}:{}/{} for {} at ",
                     httpConfig.getBindAddress(),
                     httpConfig.getPort(),
                     deployInfo.getContextPath(),
                     deployInfo.getDeploymentName());

            final Builder builder = Undertow.builder()
                           .addHttpListener(httpConfig.getPort(),
                                            httpConfig.getBindAddress(),
                                            deploymentManager.start());


            //mgmt Deployment is completely optional and will only be activated, if there is a mgmt config
            if(!mgmtConfig.isUnsatisfied() && !mgmtDeployment.isUnsatisfied()) {
                final WebServerConfiguration mCfg = mgmtConfig.get();

                LOG.info("Creating management endpoint {}:{}",
                         mCfg.getBindAddress(),
                         mCfg.getPort());

                final ServletContainer mContainer = Servlets.newContainer();
                final HttpHandler root;
                if(mgmtDeployment.isAmbiguous()) {
                    //multi-deployment
                    final PathHandler path = new PathHandler();
                    for(DeploymentInfo mDI : mgmtDeployment) {
                        final DeploymentManager mDM = mContainer.addDeployment(mDI);
                        mDM.deploy();
                        path.addPrefixPath(mDI.getContextPath(),mDM.start());
                    }
                    root = path;
                } else {
                    //single deployment
                    final DeploymentInfo mDI = mgmtDeployment.get();
                    final DeploymentManager mDM = mContainer.addDeployment(mDI);
                    mDM.deploy();
                    root = mDM.start();
                }
                builder.addHttpListener(mCfg.getPort(), mCfg.getBindAddress(), root);

            }

            return builder.build();
        } catch (final ServletException e) {
            throw new InkstandRuntimeException(e);
        }
    }

    public WebServerConfiguration getConfig() {

        return this.config;
    }

    public DeploymentInfo getDeploymentInfo() {

        return this.deploymentInfo;
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
