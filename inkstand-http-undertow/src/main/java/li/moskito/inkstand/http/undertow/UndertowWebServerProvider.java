package li.moskito.inkstand.http.undertow;

import io.undertow.Undertow;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

import li.moskito.inkstand.InkstandRuntimeException;
import li.moskito.inkstand.config.WebServerConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
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
