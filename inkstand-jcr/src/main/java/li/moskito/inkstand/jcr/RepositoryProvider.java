package li.moskito.inkstand.jcr;

import javax.enterprise.inject.Produces;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

/**
 * Interface for producers that provide an implementation of a repository. The interface is not mandatory to be
 * implemented as CDI will be triggered by the {@link Produces} annotation and a return type {@link Repository}
 * automatically.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
public interface RepositoryProvider {

    @Produces
    Repository getRepository() throws RepositoryException;
}
