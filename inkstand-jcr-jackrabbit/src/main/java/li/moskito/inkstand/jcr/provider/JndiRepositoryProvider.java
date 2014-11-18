package li.moskito.inkstand.jcr.provider;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.jcr.Repository;

import li.moskito.inkstand.jcr.JndiRepository;
import li.moskito.inkstand.jcr.RepositoryProvider;

/**
 * Provides a JCR {@link Repository} that is available as JNDI resource with the name &quot;java:/jcr/local&quot;
 * 
 * @author gmuecke
 * 
 */
@Priority(1)
public class JndiRepositoryProvider implements RepositoryProvider {

    @Resource(mappedName = "java:/jcr/local")
    private Repository repository;

    @Override
    @Produces
    @JndiRepository
    public Repository getRepository() {
        return repository;
    }
}
