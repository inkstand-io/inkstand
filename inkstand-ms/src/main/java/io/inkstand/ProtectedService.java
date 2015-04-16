package io.inkstand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;

/**
 * Stereotype to be used for {@link MicroService} components to indicate a protected or private service profile.
 * Protected services have a security mechanism in place that prevents anonymous or unauthorized access. In order to use
 * a protected service, users have to authenticate.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Stereotype
@Alternative
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE, ElementType.METHOD, ElementType.FIELD
})
public @interface ProtectedService {

}
