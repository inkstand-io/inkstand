package li.moskito.inkstand.security;

import java.security.Principal;

/**
 * A simple principal that contains only the name.
 * 
 * @author Gerald Muecke, gerald@moskito.li
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
