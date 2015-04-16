package io.inkstand.config;

import java.util.Collection;

/**
 * Configuration for the Application that should be run
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
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
