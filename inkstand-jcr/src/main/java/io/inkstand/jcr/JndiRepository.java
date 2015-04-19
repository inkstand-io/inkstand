package io.inkstand.jcr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

/**
 * Annotation to annotate a repository implementation or producer to indicate it will produce a repository that is a
 * shared resource and is accessible through a JNDI lookup.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Stereotype
@ApplicationScoped
@Alternative
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE, ElementType.METHOD, ElementType.FIELD
})
public @interface JndiRepository {

}
