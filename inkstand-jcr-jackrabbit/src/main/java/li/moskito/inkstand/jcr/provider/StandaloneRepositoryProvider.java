package li.moskito.inkstand.jcr.provider;

import java.io.File;

import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import li.moskito.inkstand.jcr.RepositoryProvider;
import li.moskito.inkstand.jcr.StandaloneRepository;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for a JCR {@link Repository} that uses a local repository configuration. The repository home directory is
 * configured using the {@code inque.jcr.home} property, either defined in the inque.properties file or via JVM
 * argument.
 *
 * @author Gerald Muecke, gerald@moskito.li
 */
@Priority(2)
public class StandaloneRepositoryProvider implements RepositoryProvider {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(StandaloneRepositoryProvider.class);

    @Inject
    @ConfigProperty(name = "inkstand.jcr.home")
    private String repositoryHome;

    @Inject
    @ConfigProperty(name = "inkstand.jcr.config")
    private String configFile;

    private RepositoryImpl repository;

    @Override
    @Produces
    @StandaloneRepository
    public Repository getRepository() throws RepositoryException {
        if (repository == null) {
            LOG.info("Connecting to local repository at {}", repositoryHome);
            final RepositoryConfig config = RepositoryConfig.create(new File(configFile), new File(repositoryHome));
            repository = RepositoryImpl.create(config);
        }
        return repository;
    }

    @PreDestroy
    public void close(@Disposes final Repository repository) {
        if (repository instanceof RepositoryImpl) {
            ((RepositoryImpl) repository).shutdown();
        }
    }
}
