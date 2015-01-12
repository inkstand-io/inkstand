package li.moskito.inkstand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launcher for an injectable microservice.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
@ApplicationScoped
public class ServiceLauncher {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLauncher.class);

    @Inject
    private MicroService microService;

    @PostConstruct
    public void init() {
        LOG.info("Starting web server");
        microService.start();
    }

    void watch(@Observes final ContainerInitialized containerInitialized) {
        LOG.debug("Container initialized");
        LOG.info("Inkstand microservice {} running", microService);
    }

    @PreDestroy
    public void shutdown() {
        microService.stop();
        LOG.info("Webserver stopped");
    }
}
