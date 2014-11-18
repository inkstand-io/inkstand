package li.moskito.inkstand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import li.moskito.inkstand.http.WebServer;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launcher for the webserver.
 * 
 * @author gmuecke
 * 
 */
@ApplicationScoped
public class ServiceLauncher {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLauncher.class);

    @Inject
    private WebServer webServer;

    @PostConstruct
    public void init() {
        LOG.info("Starting web server");
        webServer.start();
    }

    void watch(@Observes final ContainerInitialized containerInitialized) {
        LOG.debug("Container initialized");
    }

    @PreDestroy
    public void shutdown() {
        webServer.stop();
        LOG.info("Webserver stopped");
    }
}
