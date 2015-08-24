package io.inkstand.cdi;

import static org.slf4j.LoggerFactory.getLogger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.servlet.annotation.WebServlet;
import javax.ws.rs.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

/**
 * Created by Gerald Mücke on 24.08.2015.
 */
public class WebServlets implements Extension  {

    private static final Logger LOG = getLogger(WebServlets.class);

    private final Set<Class> servlets = new HashSet<>();

    /**
     * Collects a found {@link Path} resource
     *
     * @param pat
     *  the event for the type that is annotated with the {@link Path}  annotation
     */
    public void servletFound(@Observes
                          @WithAnnotations(WebServlet.class) final ProcessAnnotatedType pat) {
        LOG.debug("Discovered servlet {}", pat.getAnnotatedType().getJavaClass());
        servlets.add(pat.getAnnotatedType().getJavaClass());
    }

    public Set<Class> getServlets() {

        return Collections.unmodifiableSet(servlets);
    }
}
