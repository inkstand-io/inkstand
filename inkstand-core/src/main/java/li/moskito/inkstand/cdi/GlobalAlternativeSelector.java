package li.moskito.inkstand.cdi;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
import org.jboss.weld.environment.deployment.WeldDeployment;
import org.jboss.weld.environment.deployment.WeldResourceLoader;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.xml.BeansXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalAlternativeSelector implements Extension {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(GlobalAlternativeSelector.class);

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
        final ResourceLoader loader = new WeldResourceLoader();
        final URL beansXmlUrl = loader.getResource(WeldDeployment.BEANS_XML);
        if (beansXmlUrl == null) {
            throw new IllegalStateException("No beans.xml found");
        }
        final BeansXml beansXml = new BeansXmlParser().parse(beansXmlUrl);
        enabledStereotypes.addAll(loadClasses(beansXml.getEnabledAlternativeStereotypes()));
        enabledAlternatives.addAll(loadClasses(beansXml.getEnabledAlternativeClasses()));
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
            final String alternativeClassName = alternative.getValue();
            try {
                classes.add((Class<? extends Annotation>) Class.forName(alternativeClassName));
            } catch (final ClassNotFoundException e) {
                throw new IllegalStateException("Alternative  " + alternativeClassName + " not found", e);
            }
        }
        return classes;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void watchAlternatives(
            @Observes @WithAnnotations({ Stereotype.class, Alternative.class }) final ProcessAnnotatedType pat) {
        final AnnotatedType type = pat.getAnnotatedType();
        if (enabledAlternatives.contains(type.getJavaClass())
                || matchesEnabledStereotypes(type)
                || matchesEnabledStereotypes(type.getMethods())
                || matchesEnabledStereotypes(type.getFields())) {
            LOG.info("Enabled alternative {}", pat.getAnnotatedType());
            return;
        }
        LOG.info("Disabledalternative {}", pat.getAnnotatedType());
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

    void afterTypeDiscovery(@Observes final AfterTypeDiscovery atd, final BeanManager bm) {
        LOG.debug("Discovered Alternatives {}", atd.getAlternatives());
    }

    // void event(@Observes final AfterBeanDiscovery event) {
    // LOG.info("AfterBeanDiscovery {}", event);
    // }
    //
    // void event(@Observes final AfterDeploymentValidation event) {
    // LOG.info("AfterDeploymentValidation {}", event);
    // }

}
