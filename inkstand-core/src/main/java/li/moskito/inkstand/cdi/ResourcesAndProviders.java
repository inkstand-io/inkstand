package li.moskito.inkstand.cdi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

/**
 * The extension collects all REST Resources ({@link Path} annotated) and {@link Provider}s. The class may be injected. <br>
 * The class is inspired by : ws.ament.hammock.core.impl.ClassScannerExtension
 */
@SuppressWarnings("rawtypes")
public class ResourcesAndProviders implements Extension {

    private final Set<Class> resources = new HashSet<>();
    private final Set<Class> providers = new HashSet<>();

    /**
     * Collects a found {@link Path} resource
     * 
     * @param pat
     */
    public void pathFound(@Observes @WithAnnotations(Path.class) final ProcessAnnotatedType pat) {
        this.resources.add(pat.getAnnotatedType().getJavaClass());
    }

    /**
     * Collects a found {@link Provider} .
     * 
     * @param pat
     */
    public void providerFound(@Observes @WithAnnotations(Provider.class) final ProcessAnnotatedType pat) {
        this.providers.add(pat.getAnnotatedType().getJavaClass());
    }

    /**
     * @return all found provider classes
     */
    public Collection<Class> getProviderClasses() {
        return providers;
    }

    /**
     * 
     * @return all found resources
     */
    public Collection<Class> getResourceClasses() {
        return resources;
    }

}
