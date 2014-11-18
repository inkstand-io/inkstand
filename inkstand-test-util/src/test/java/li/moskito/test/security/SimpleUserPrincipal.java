package li.moskito.test.security;

import java.security.Principal;

/**
 * A Principal to be used in test that carries a username information.
 * 
 * @author gmuecke
 * 
 */
public class SimpleUserPrincipal implements Principal {

    private final String name;

    public SimpleUserPrincipal(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
