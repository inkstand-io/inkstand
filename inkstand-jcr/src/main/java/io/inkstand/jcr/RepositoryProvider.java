package io.inkstand.jcr;

import javax.enterprise.inject.Produces;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

/**
 * Interface for producers that provide an implementation of a repository. The interface is not mandatory to be
 * implemented as CDI will be triggered by the {@link Produces} annotation and a return type {@link Repository}
 * automatically.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public interface RepositoryProvider {

    @Produces
    Repository getRepository() throws RepositoryException;
}
