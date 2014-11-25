package li.moskito.inkstand.config;

import java.util.Collection;

/**
 * Configuration for the Application that should be run
 * 
 * @author Gerald Muecke, gerald@moskito.li
 * 
 */
public interface ApplicationConfiguration {

    /**
     * Context root
     * 
     * @return
     */
    public String getContextRoot();

    /**
     * The list of provider classes to register.
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Collection<Class> getProviderClasses();

    /**
     * The list of resource classes to load.
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Collection<Class> getResourceClasses();
}
