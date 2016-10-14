/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.mgmt;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;

/**
 * Injection utiltiy for making CDI handling more convenient.
 * Created by Gerald Mücke on 17.09.2015.
 */
public final class Injector {
    //TODO candidate to be moved to a cdi util
    private Injector(){}

    /**
     * Adds the unmanaged instance to the dependency context, satisfying all missing dependencies of the
     * unmanaged instance as long as matching dependency providers are available in the context.
     * @param unmanagedInstance
     *  the instance to be added to the context.
     */
    public static void addToContext(final Object unmanagedInstance) {
        final BeanManager beanManager = getBeanManager();
        final CreationalContext creationalContext = beanManager.createCreationalContext(null);
        final AnnotatedType annotatedType = beanManager.createAnnotatedType(unmanagedInstance.getClass());
        final InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(unmanagedInstance, creationalContext);
    }

    private static BeanManager getBeanManager() {

        final CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
        return cdiContainer.getBeanManager();
    }

    /**
     * Resolves an instance of the specified from the active CDI container.
     * @param type
     *  the type of the bean that should be retrieved.
     * @param <T>
     *  the type of the bean instance
     * @return
     *  an instance of the resolved bean
     */
    public static <T> T getBeanInstance(Class<? extends T> type){
        final BeanManager beanManager = getBeanManager();
        final Bean<T> bean = (Bean<T>) beanManager.resolve(beanManager.getBeans(type));
        final T beanInstace = (T) beanManager.getReference(bean, bean.getBeanClass(), beanManager
                .createCreationalContext(bean));
        return beanInstace;
    }
}
