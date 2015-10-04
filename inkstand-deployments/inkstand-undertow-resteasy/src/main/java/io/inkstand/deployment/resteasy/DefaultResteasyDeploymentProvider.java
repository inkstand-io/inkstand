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

package io.inkstand.deployment.resteasy;

import javax.annotation.Priority;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.inkstand.PublicService;
import io.inkstand.config.ApplicationConfiguration;
import io.inkstand.http.undertow.UndertowDeploymentProvider;
import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletInfo;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import ws.ament.hammock.core.impl.CDIListener;

/**
 * Provider for producing an {@link DeploymentInfo} for an {@link Undertow} web server that provides Jax-RS support
 * based on RestEasy. The deployment allows to host Jax-RS resources and providers in a webserver.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Singleton
public class DefaultResteasyDeploymentProvider implements UndertowDeploymentProvider {

    @Inject
    private ApplicationConfiguration appConfig;

    @Override
    @Produces
    public DeploymentInfo getDeployment() {

        final ResteasyDeployment deployment = new ResteasyDeployment();

        final ApplicationConfiguration config = this.getAppConfig();

        deployment.getActualResourceClasses().addAll(config.getResourceClasses());
        deployment.getActualProviderClasses().addAll(config.getProviderClasses());
        deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());

        final ListenerInfo listener = Servlets.listener(CDIListener.class);

        //@formatter:off
        final ServletInfo resteasyServlet = Servlets.servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
                .setAsyncSupported(true)
                .setLoadOnStartup(1)
                .addInitParam("org.jboss.weld.environment.servlet.archive.isolation", "true")
                .addMapping("/*");

        return new DeploymentInfo()
        .addListener(listener)
        .setContextPath(config.getContextRoot())
        .addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
        .addServlet(resteasyServlet)
        .setDeploymentName("ResteasyUndertow")
        .setClassLoader(ClassLoader.getSystemClassLoader());
        // @formatter:on

    }

    public ApplicationConfiguration getAppConfig() {

        return this.appConfig;
    }

    /**
     * Stereotyped version of the default DefaultResteasyDeploymentProvider that can be activated using the {@link
     * PublicService} stereotype in beans.xml.
     */
    @Priority(1)
    @PublicService
    @Singleton
    private static class PublicResteaysDeploymentProvider extends DefaultResteasyDeploymentProvider {

    }
}
