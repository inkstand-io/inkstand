package io.inkstand.deployment.resteasy;

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletInfo;

import javax.annotation.Priority;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.inkstand.ProtectedService;
import io.inkstand.config.ApplicationConfiguration;
import io.inkstand.http.undertow.UndertowDeploymentProvider;

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
@ProtectedService
@Priority(0)
public class BasicSecurityResteasyDeploymentProvider implements UndertowDeploymentProvider {

    // TODO make security configuraiton configurable

    @Inject
    private ApplicationConfiguration appConfig;

    @Override
    @Produces
    public DeploymentInfo getDeployment() {

        // new UndertowJaxrsServer();

        final ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.getActualResourceClasses().addAll(appConfig.getResourceClasses());
        deployment.getActualProviderClasses().addAll(appConfig.getProviderClasses());
        deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
        deployment.setSecurityEnabled(true);

        final ListenerInfo listener = Servlets.listener(CDIListener.class);

        //@formatter:off
        final ServletInfo resteasyServlet = Servlets.servlet("ResteasyServlet", HttpServlet30Dispatcher.class)
                .setAsyncSupported(true)
                .setLoadOnStartup(1)
                .addInitParam("org.jboss.weld.environment.servlet.archive.isolation", "true")
                .addMapping("/*");
        resteasyServlet.addSecurityRoleRef("User", null);

        final DeploymentInfo di =  new DeploymentInfo()
            .setClassLoader(ClassLoader.getSystemClassLoader())
            .addListener(listener)
            .setDeploymentName("ResteasyUndertow")
            .setContextPath(appConfig.getContextRoot())
            .addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
            .addServlet(resteasyServlet)
            .setLoginConfig(Servlets.loginConfig("My Realm").addFirstAuthMethod("BASIC"))
            .addSecurityRole("Users")
            .addSecurityConstraint(Servlets.securityConstraint().addRoleAllowed("Users")
            .addWebResourceCollection(Servlets.webResourceCollection().addUrlPattern("/*")));

        // @formatter:on

        return di;

    }
}
