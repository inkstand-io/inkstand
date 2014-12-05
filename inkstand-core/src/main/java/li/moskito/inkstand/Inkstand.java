package li.moskito.inkstand;

/**
 * Inkstand launcher. Alternative way of starting Weld SE Container.
 * 
 * @author Gerald Muecke, gerald@moskito.li
 */
public final class Inkstand {
    private Inkstand() {
    }

    public static void main(final String[] args) {
        org.jboss.weld.environment.se.StartMain.main(args);
    }
}
