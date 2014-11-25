package li.moskito.inkstand.cdi;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.jboss.weld.bootstrap.spi.BeansXml;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.xml.BeansXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     */
    public void loadApplicationAlternatives(@Observes final BeforeBeanDiscovery bbd) {
        LOG.info("starting bean discovery");

        final URL beansXmlUrl = getResource(BEANS_XML);
        if (beansXmlUrl == null) {
            throw new IllegalStateException("No beans.xml found");
        }
        final BeansXml beansXml = new BeansXmlParser().parse(beansXmlUrl);
        enabledStereotypes.addAll(loadClasses(beansXml.getEnabledAlternativeStereotypes()));
        enabledAlternatives.addAll(loadClasses(beansXml.getEnabledAlternativeClasses()));
    }

    private URL getResource(final String name) {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl != null) {
            return cl.getResource(name);
        } else {
            return getClass().getResource(name);
        }
    }

    /**
     * Loads the clases from the set of set of metadata
     * 
     * @param enabledAlternatives
     *            metadata definitions of the alternative classes
     * @return set of classes loaded from the metadata
     */
    @SuppressWarnings("unchecked")
    private Collection<Class<? extends Annotation>> loadClasses(final Iterable<Metadata<String>> enabledAlternatives) {
        final Set<Class<? extends Annotation>> classes = new HashSet<>();
        for (final Metadata<String> alternative : enabledAlternatives) {
            LOG.info("Loading alternative {}", alternative);
            final String alternativeClassName = alternative.getValue();
            try {
                classes.add((Class<? extends Annotation>) Class.forName(alternativeClassName));
            } catch (final ClassNotFoundException e) {
                throw new IllegalStateException("Alternative  " + alternativeClassName + " not found", e);
            }
        }
        return classes;
    }

    /**
     * Watches all alternatives. If a cross-bda alternative is marked with the {@link Priority} annotation it will be
     * consideres as Application-Scoped alternative. If the alternative class itself or its stereotype matches the
     * enabled alternative, it will be accepted, otherwise it will be vetoed.
     * 
     * @param pat
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void watchAlternatives(
            @Observes @WithAnnotations({ Stereotype.class, Alternative.class }) final ProcessAnnotatedType pat) {
        final AnnotatedType type = pat.getAnnotatedType();
        // any non-priority alternative will be handles normally
        if (!type.isAnnotationPresent(Priority.class)
                || enabledAlternatives.contains(type.getJavaClass())
                || matchesEnabledStereotypes(type)
                || matchesEnabledStereotypes(type.getMethods())
                || matchesEnabledStereotypes(type.getFields())) {
            LOG.info("Enable alternative {}", pat.getAnnotatedType());
            return;
        }
        LOG.info("Disable alternative {}", pat.getAnnotatedType());
        pat.veto();
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
     * @param annotated
     *            the annotated elemens to check
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

    public void afterTypeDiscovery(@Observes final AfterTypeDiscovery atd, final BeanManager bm) {
        LOG.info("Discovered Alternatives {}", atd.getAlternatives());
    }

}
