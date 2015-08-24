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
 * Provider to provide the default management deployment containing all management servlets.
 * Created by Gerald Mücke on 21.08.2015.
 */
public class ManagementDeploymentProvider implements UndertowDeploymentProvider {

    @Inject
    private WebServlets servlets;

    @Produces
    @Management
    public DeploymentInfo getDeployment() {

        final DeploymentInfo di = new DeploymentInfo();

        for(Class servletClass : servlets.getServlets(Management.class)) {
            final WebServlet servletDesc = (WebServlet) servletClass.getAnnotation(WebServlet.class);
            final ServletInfo si = createServletInfo(servletDesc);
            di.addServlet(si);

        }
        di.setDeploymentName("Management Console");
        di.setContextPath("/mgmt");
        di.setClassLoader(ClassLoader.getSystemClassLoader());
        return di;
    }

    /**
     * Creates the servlet info from the servlet descriptor to be added to the deployment info for
     * the undertow deployment manager. The method will extract url mappings, init parameters,
     * async-settings and load-on-startup behavior from the descriptor.
     * @param servletDesc
     *  the servlet descriptor annotation from the servlet class
     * @return
     *  the ServletInfo to be added to a deployment.
     */
    private ServletInfo createServletInfo(final WebServlet servletDesc) {

        //TODO candidate for Undertow utility class

        final ServletInfo si = new ServletInfo(servletDesc.name(), ManagementServlet.class);
        si.addMappings(servletDesc.urlPatterns());
        for(WebInitParam param : servletDesc.initParams()) {
            si.addInitParam(param.name(), param.value());
        }
        si.setAsyncSupported(servletDesc.asyncSupported());
        si.setLoadOnStartup(servletDesc.loadOnStartup());
        return si;
    }
}
