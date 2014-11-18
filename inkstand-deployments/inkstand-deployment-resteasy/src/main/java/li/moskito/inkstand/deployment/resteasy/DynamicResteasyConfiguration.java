package li.moskito.inkstand.deployment.resteasy;

import java.util.Collection;

import javax.inject.Inject;

import li.moskito.inkstand.cdi.ResourcesAndProviders;
import li.moskito.inkstand.config.ApplicationConfiguration;

import org.apache.deltaspike.core.api.config.ConfigProperty;

public class DynamicResteasyConfiguration implements ApplicationConfiguration {

    @Inject
    @ConfigProperty(name = "inkstand.rest.contextRoot",
            defaultValue = "")
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
        return contexRoot == null ? "" : contexRoot;
    }

}
