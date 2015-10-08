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

package io.inkstand.cdi;

import javax.annotation.Priority;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.xml.BeansXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CDI Extension that scans detects global alternatives (annotated with {@link Priority}) and disables those, that are
 * not defined as alternative or stereotype in the beans.xml of the application.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class GlobalAlternativeSelector implements Extension {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(GlobalAlternativeSelector.class);

    /**
     * Location of beans.xml
     */
    private static final String BEANS_XML = "META-INF/beans.xml";
    /**
     * Stereotype classes that enable an application alternative
     */
    private final Set<Class<? extends Annotation>> enabledStereotypes = new HashSet<>();
    /**
     * Classes that denote an alternative class
     */
    private final Set<Class<?>> enabledAlternatives = new HashSet<>();

    /**
     * Loads the application-level alternative classes and stereotypes.
     * 
     * @param bbd
     *  the event that is fired before the discovery of beans begin
     */
    public void loadApplicationAlternatives(@Observes final BeforeBeanDiscovery bbd) {
        LOG.debug("starting bean discovery");

        final URL beansXmlUrl = getBeansXmlResource();
        if (beansXmlUrl == null) {
            throw new IllegalStateException("No beans.xml found");
        }
        //TODO the processing of the beans.xml is depending on Weld/RI this should be removed
        // at latest when alternative CDI containers (OpenWebBeans) have to be supported as well
        final BeansXml beansXml = new BeansXmlParser().parse(beansXmlUrl);
        enabledStereotypes.addAll(loadClasses(beansXml.getEnabledAlternativeStereotypes()));
        enabledAlternatives.addAll(loadClasses(beansXml.getEnabledAlternativeClasses()));
    }

    private URL getBeansXmlResource() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            return cl.getResource(BEANS_XML);
        } else {
            return getClass().getResource(BEANS_XML);
        }
    }

    /**
     * Loads the classes from the set of set of metadata
     * 
     * @param enabledAlternatives
     *            metadata definitions of the alternative classes
     * @return set of classes loaded from the metadata
     */
    @SuppressWarnings("unchecked")
    private Collection<Class<Annotation>> loadClasses(final Iterable<Metadata<String>> enabledAlternatives) {
        final Set<Class<Annotation>> classes = new HashSet<>();
        for (final Metadata<String> alternative : enabledAlternatives) {
            LOG.debug("Loading alternative {}", alternative);
            final String alternativeClassName = alternative.getValue();
            try {
                classes.add((Class<Annotation>) Class.forName(alternativeClassName));
            } catch (final ClassNotFoundException e) {
                throw new IllegalStateException("Alternative  " + alternativeClassName + " not found", e);
            }
        }
        return classes;
    }

    /**
     * Watches all alternatives. If a cross-bda alternative is marked with the {@link Priority} annotation it will be
     * considered as Application-Scoped alternative. If the alternative class itself or its stereotype matches the
     * enabled alternative, it will be accepted, otherwise it will be vetoed.
     * 
     * @param pat
     *  the CDI descriptor of a annotated type that is processed by the CDI container on initialization
     */
    @SuppressWarnings("rawtypes")
    public void watchAlternatives(@Observes @WithAnnotations({
            Stereotype.class, Alternative.class
    }) final ProcessAnnotatedType pat) {
        final AnnotatedType type = pat.getAnnotatedType();
        // any non-priority alternative will be handles normally
        if (!type.isAnnotationPresent(Priority.class) || enabledAlternatives.contains(type.getJavaClass())
                || matchesAnyEnabledStereotype(type)) {
            LOG.debug("Enable alternative {}", pat.getAnnotatedType());
            return;
        }
        LOG.debug("Disable alternative {}", pat.getAnnotatedType());
        pat.veto();
    }

    /**
     * Checks, if the {@link AnnotatedType}, its fields or methods, matches any of the enabled stereotypes of the
     * beans.xml
     * 
     * @param type
     *            the type to verify
     * @return <code>true</code> if it matches a type
     */
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    private boolean matchesAnyEnabledStereotype(final AnnotatedType type) {
        return matchesEnabledStereotypes(type) || matchesEnabledStereotypes(type.getMethods())
                || matchesEnabledStereotypes(type.getFields());
    }

    /**
     * Checks a set of annotated elements if they are annotated with {@link Produces} and if any of the enabled
     * {@link Stereotype}s
     * 
     * @param annotated
     *            the set of annotated elements to check
     * @return <code>true</code> if any of the annotated elements matches the enabled stereotypes
     */
    private boolean matchesEnabledStereotypes(final Set<Annotated> annotated) {
        for (final Annotated an : annotated) {
            if (an.isAnnotationPresent(Produces.class) && matchesEnabledStereotypes(an)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks an annotated element if it is are annotated with any of the enabled {@link Stereotype}s
     * 
     * @param type
     *            the annotated element to check
     * @return <code>true</code> if the annotated element matches the enabled stereotypes
     */
    private boolean matchesEnabledStereotypes(final Annotated type) {
        for (final Class<? extends Annotation> stereotype : enabledStereotypes) {
            if (type.isAnnotationPresent(stereotype)) {
                return true;
            }
        }
        return false;
    }

    public void afterTypeDiscovery(@Observes final AfterTypeDiscovery atd) {
        LOG.info("Discovered Alternatives {}", atd.getAlternatives());
    }

}
