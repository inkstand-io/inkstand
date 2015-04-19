package io.inkstand.jcr.provider;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.jcr.Repository;

import io.inkstand.jcr.JndiRepository;
import io.inkstand.jcr.RepositoryProvider;

/**
 * Provides a JCR {@link Repository} that is available as JNDI resource with the name &quot;java:/jcr/local&quot;
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 * 
 */
@Priority(3)
public class JndiRepositoryProvider implements RepositoryProvider {

	//TODO find a generic way to point to a the JCR Repo via JNDI
    @Resource(mappedName = "java:/jcr/local/node01")
    private Repository repository;

    @Override
    @Produces
    @JndiRepository
    public Repository getRepository() {
        return repository;
    }
}
