/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.cdi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The extension collects all REST Resources ({@link Path} annotated) and {@link Provider}s. The class may be injected. <br>
 * The class is inspired by : ws.ament.hammock.core.impl.ClassScannerExtension
 */
@SuppressWarnings("rawtypes")
public class ResourcesAndProviders implements Extension {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(ResourcesAndProviders.class);

    private final Set<Class> resources = new HashSet<>();
    private final Set<Class> providers = new HashSet<>();

    /**
     * Collects a found {@link Path} resource
     *
     * @param pat
     *  the event for the type that is annotated with the {@link Path}  annotation
     */
    public void pathFound(@Observes @WithAnnotations(Path.class) final ProcessAnnotatedType pat) {
        LOG.debug("Discovered resource {}", pat.getAnnotatedType().getJavaClass());
        resources.add(pat.getAnnotatedType().getJavaClass());
    }

    /**
     * Collects a found {@link Provider} .
     *
     * @param pat
     *  the event for the type that is annotated with the {@link Provider} annotation
     */
    public void providerFound(@Observes @WithAnnotations(Provider.class) final ProcessAnnotatedType pat) {
        LOG.debug("Discovered provider {}", pat.getAnnotatedType().getJavaClass());
        providers.add(pat.getAnnotatedType().getJavaClass());
    }

    /**
     * @return all found provider classes
     */
    public Collection<Class> getProviderClasses() {
        return providers;
    }

    /**
     * @return all found resources
     */
    public Collection<Class> getResourceClasses() {
        return resources;
    }

}
