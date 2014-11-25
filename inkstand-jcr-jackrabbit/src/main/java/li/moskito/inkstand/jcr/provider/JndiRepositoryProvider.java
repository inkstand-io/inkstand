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
 * @author Gerald Muecke, gerald@moskito.li
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
