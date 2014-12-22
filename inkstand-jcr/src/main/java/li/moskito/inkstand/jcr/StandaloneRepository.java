package li.moskito.inkstand.jcr;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

/**
 * Annotation to annotate a repository implementation or producer to indicate it will produce a standalone repository
 * that has it's own and exclusive repository configuration and home directory.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
@Stereotype
@ApplicationScoped
@Alternative
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE, ElementType.METHOD, ElementType.FIELD
})
public @interface StandaloneRepository {

}
