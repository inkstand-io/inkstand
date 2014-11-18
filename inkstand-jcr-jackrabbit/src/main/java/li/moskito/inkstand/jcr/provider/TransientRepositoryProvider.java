package li.moskito.inkstand.jcr.provider;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import li.moskito.inkstand.jcr.RepositoryProvider;
import li.moskito.inkstand.jcr.util.JCRUtil;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider that provides a transient, in-memory repository that is not persisted
 * 
 * @author gmuecke
 * 
 */
@Priority(1)
public class TransientRepositoryProvider implements RepositoryProvider {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(TransientRepositoryProvider.class);

    private TransientRepository repository;
    private Path tempFolder;

    @Inject
    @ConfigProperty(name = "inkstand.jcr.transient.configURL")
    private String configURL;

    @Override
    @Produces
    @li.moskito.inkstand.jcr.TransientRepository
    public Repository getRepository()
            throws RepositoryException {
        return repository;
    }

    @PostConstruct
    public void startRepository() {
        LOG.info("Creating transient repositor");
        try {
            initializeRepository();
            loadContentModel();
            // TODO make data file configurable
        } catch (IOException | RepositoryException | ParseException e) {
            throw new RuntimeException("Could not start repository", e);
        }

    }

    @PreDestroy
    public void shutdownRepository() {

        try {
            if (repository != null) {
                repository.shutdown();
            }
            FileUtils.deleteDirectory(tempFolder.toFile());
        } catch (final IOException e) {
            throw new RuntimeException("Could not cleanup temp folder", e);
        }
    }

    /**
     * Creates a transient test repository for integration testing
     * 
     * @throws IOException
     * @throws ConfigurationException
     */
    private void initializeRepository()
            throws IOException, ConfigurationException {
        this.tempFolder = Files.createTempDirectory("inque");
        final URL configLocation = TransientRepositoryProvider.class.getResource(configURL);
        repository = JCRUtil.createTransientRepository(tempFolder.toFile(), configLocation);
    }

    /**
     * Initializes the Test Repository with the Inque nodetype model
     * 
     * @throws RepositoryException
     * @throws IOException
     * @throws ParseException
     */
    private void loadContentModel()
            throws RepositoryException, IOException, ParseException {
        final Session adminSession = createAdminSession();
        JCRUtil.initializeContentModel(adminSession);
        adminSession.logout();

    }

    private Session createAdminSession()
            throws RepositoryException {
        final Session adminSession = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        return adminSession;
    }

}
