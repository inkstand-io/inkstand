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

import io.inkstand.PublicService;
import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import javax.annotation.Priority;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

import io.inkstand.InkstandRuntimeException;
import io.inkstand.config.WebServerConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider of an Undertow WebServer instance with a specific deployment configuration. The deployment configuration is
 * injected itself and may be provided by an implementation of {@link UndertowDeploymentProvider}. The {@link Undertow}
 * instance provided by this provider is only configured, but not started.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Singleton
@Priority(0)
@PublicService
public class UndertowWebServerProvider {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(UndertowWebServerProvider.class);
    @Inject
    private WebServerConfiguration config;

    @Inject
    private DeploymentInfo deploymentInfo;

    @Produces
    public Undertow getUndertow() {

        final DeploymentManager deploymentManager = Servlets.defaultContainer().addDeployment(deploymentInfo);
        deploymentManager.deploy();

        try {
            LOG.info("Creating service endpoint {}:{}/{} for {} at ", config.getBindAddress(), config.getPort(),
                    deploymentInfo.getContextPath(), deploymentInfo.getDeploymentName());
            return Undertow.builder().addHttpListener(config.getPort(), config.getBindAddress())
                    .setHandler(deploymentManager.start()).build();
        } catch (final ServletException e) {
            throw new InkstandRuntimeException(e);
        }
    }
}
