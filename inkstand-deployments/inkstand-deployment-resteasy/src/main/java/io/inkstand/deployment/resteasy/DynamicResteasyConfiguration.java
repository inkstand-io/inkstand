package io.inkstand.deployment.resteasy;

import java.util.Collection;

import javax.inject.Inject;

import io.inkstand.cdi.ResourcesAndProviders;
import io.inkstand.config.ApplicationConfiguration;

import org.apache.deltaspike.core.api.config.ConfigProperty;

/**
 * {@link ApplicationConfiguration} for setting up the Resteasy deployment configuration (i.e. the
 * {@link DefaultResteasyDeploymentProvider}). This implentation provides a context root (property
 * <code>inkstand.rest.contextRoot</code>) that is injected as DeltaSpike {@link ConfigProperty} and which is an empty
 * string as default value. <br>
 * The Resource and Provider classes are injected by the {@link ResourcesAndProviders} CDI extension that scans the
 * classpath and detects matching classes on bootstrap automatically.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class DynamicResteasyConfiguration implements ApplicationConfiguration {

    @Inject
    @ConfigProperty(name = "inkstand.rest.contextRoot", defaultValue = "")
    private String contexRoot;

    @Inject
    private ResourcesAndProviders scanner;

    @SuppressWarnings("rawtypes")
    @Override
    public Collection<Class> getProviderClasses() {
        return scanner.getProviderClasses();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Collection<Class> getResourceClasses() {
        return scanner.getResourceClasses();
    }

    @Override
    public String getContextRoot() {
        return contexRoot == null
                ? ""
                : contexRoot;
    }

}
