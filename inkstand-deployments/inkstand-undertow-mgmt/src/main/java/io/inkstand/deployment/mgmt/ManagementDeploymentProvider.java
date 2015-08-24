package io.inkstand.deployment.mgmt;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import io.inkstand.Management;
import io.inkstand.cdi.WebServlets;
import io.inkstand.http.undertow.UndertowDeploymentProvider;
import io.inkstand.mgmt.ManagementServlet;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;

/**
 * Provider to provide the default management web servlet.
 * Created by Gerald Mücke on 21.08.2015.
 */
public class ManagementDeploymentProvider implements UndertowDeploymentProvider {

    @Inject
    private WebServlets servlets;

    @Produces
    @Management
    public DeploymentInfo getDeployment() {

        final DeploymentInfo di = new DeploymentInfo();

        for(Class servletClass : servlets.getServlets()) {
            final WebServlet servletDesc = ManagementServlet.class.getAnnotation(WebServlet.class);
            final ServletInfo si = new ServletInfo(servletDesc.name(), ManagementServlet.class);
            si.addMappings(servletDesc.urlPatterns());
            for(WebInitParam param : servletDesc.initParams()) {
                si.addInitParam(param.name(), param.value());
            }
            si.setAsyncSupported(servletDesc.asyncSupported());
            si.setLoadOnStartup(servletDesc.loadOnStartup());
            di.addServlet(si);

        }
        di.setDeploymentName("Management Console");
        di.setContextPath("/mgmt");
        di.setClassLoader(ClassLoader.getSystemClassLoader());
        return di;
    }
}
