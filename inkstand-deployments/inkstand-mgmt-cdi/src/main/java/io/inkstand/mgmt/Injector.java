package io.inkstand.mgmt;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;

/**
 * Injection utiltiy for making CDI handling more convenient.
 * Created by Gerald Mücke on 17.09.2015.
 */
public class Injector {
    //TODO candidate to be moved to a cdi util
    private Injector(){}

    /**
     * Adds the unmanaged instance to the dependency context, satisfying all missing dependencies of the
     * unmanaged instance as long as matching dependency providers are available in the context.
     * @param unmanagedInstance
     *  the instance to be added to the context.
     */
    public static void addToContext(final Object unmanagedInstance) {
        final CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
        final BeanManager beanManager = cdiContainer.getBeanManager();
        final CreationalContext creationalContext = beanManager.createCreationalContext(null);
        final AnnotatedType annotatedType = beanManager.createAnnotatedType(unmanagedInstance.getClass());
        final InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(unmanagedInstance, creationalContext);
    }

}
