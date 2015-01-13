package li.moskito.inkstand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

/**
 * Stereotype to be used for {@link MicroService} components to indicate a public service profile. The public service
 * has no restrictions on the content served and therefore anonymous access is granted to everything and no security
 * (authentication, authorization) is in place.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
@Stereotype
@Alternative
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE, ElementType.METHOD, ElementType.FIELD
})
public @interface PublicService {

}
