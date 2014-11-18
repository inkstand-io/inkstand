package li.moskito.inkstand.jcr;

import javax.enterprise.inject.Produces;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

public interface RepositoryProvider {

    @Produces
    Repository getRepository()
            throws RepositoryException;
}
