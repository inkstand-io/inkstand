package io.inkstand;

/**
 * Inkstand launcher. Alternative way of starting Weld SE Container.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public final class Inkstand {
    private Inkstand() {
    }

    public static void main(final String[] args) {
        org.jboss.weld.environment.se.StartMain.main(args);
    }
}
