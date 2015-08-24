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

    public Set<Class> getServlets(Class... qualifiers){

        if(qualifiers.length == 0){
            return Collections.unmodifiableSet(servlets);
        }

        Set<Class> result = new HashSet<>();
        for(Class servletClass : servlets){
            if(matchesAnnotations(servletClass, qualifiers)) {
                result.add(servletClass);
            }
        }

        return result;
    }

    /**
     * Checks if the servletClass is annotated with ALL of the specified annotations.
     * @param servletClass
     *  the servlet class to check for annotations
     * @param qualifiers
     *  the qualifier annotations that are all expected to be found on the servlet class
     * @return
     *  <code>true</code> if all annotations were found on the servlet class
     */
    private boolean matchesAnnotations(final Class servletClass, final Class[] qualifiers) {

        for(Class qualifier : qualifiers) {
            if(servletClass.getAnnotation(qualifier) == null) {
                return false;
            }
        }
        return true;
    }
}
