package io.inkstand.security;

import java.security.Principal;

/**
 * A simple principal that contains only the name.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class SimplePrincipal implements Principal {

    private final String name;

    public SimplePrincipal(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
