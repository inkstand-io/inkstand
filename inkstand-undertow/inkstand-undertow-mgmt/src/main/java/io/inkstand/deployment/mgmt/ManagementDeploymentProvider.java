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

package io.inkstand.deployment.mgmt;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import org.apache.deltaspike.cdise.servlet.CdiServletRequestListener;

import io.inkstand.Management;
import io.inkstand.cdi.WebServlets;
import io.inkstand.http.undertow.UndertowDeploymentProvider;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletInfo;

/**
 * Provider to provide the default management deployment containing all management servlets.
 * Created by Gerald Mücke on 21.08.2015.
 */
public class ManagementDeploymentProvider implements UndertowDeploymentProvider {

    @Inject
    private WebServlets servlets;

    @Override
    @Produces
    @Management
    public DeploymentInfo getDeployment() {

        final DeploymentInfo di = new DeploymentInfo();

        for(Class servletClass : servlets.getServlets(Management.class)) {
            final ServletInfo si = createServletInfo(servletClass);
            di.addServlet(si);

        }

        final ListenerInfo listenerInfo = Servlets.listener(CdiServletRequestListener.class);
        di.addListener(listenerInfo);
        di.setDeploymentName("Management Console");
        di.setContextPath("/inkstand/servlet");
        di.setClassLoader(ClassLoader.getSystemClassLoader());
        return di;
    }

    /**
     * Creates the servlet info from the servlet descriptor to be added to the deployment info for
     * the undertow deployment manager. The method will extract url mappings, init parameters,
     * async-settings and load-on-startup behavior from the descriptor.
     * @param servletClass
     *  the servlet class that is annotated with {@link WebServlet}
     * @return
     *  the ServletInfo to be added to a deployment.
     */
    private ServletInfo createServletInfo(final Class servletClass) {

        //TODO candidate for Undertow utility class
        final WebServlet servletDesc = (WebServlet) servletClass.getAnnotation(WebServlet.class);

        final ServletInfo si = new ServletInfo(servletDesc.name(), servletClass);
        si.addMappings(servletDesc.urlPatterns());
        for(WebInitParam param : servletDesc.initParams()) {
            si.addInitParam(param.name(), param.value());
        }
        si.setAsyncSupported(servletDesc.asyncSupported());
        si.setLoadOnStartup(servletDesc.loadOnStartup());
        return si;
    }
}
