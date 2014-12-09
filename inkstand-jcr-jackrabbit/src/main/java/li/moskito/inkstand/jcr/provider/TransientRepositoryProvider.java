package li.moskito.inkstand.jcr.provider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import li.moskito.inkstand.InkstandRuntimeException;
import li.moskito.inkstand.jcr.RepositoryProvider;

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
 * @author Gerald Muecke, gerald@moskito.li
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

    @Inject
    @ConfigProperty(name = "inkstand.jcr.transient.cndFileURL")
    private String cndFileURL;

    private Session adminSession;

    @Override
    @Produces
    @li.moskito.inkstand.jcr.TransientRepository
    public Repository getRepository() throws RepositoryException {
        return repository;
    }

    @PostConstruct
    public void startRepository() {
        LOG.info("Creating transient repository");
        try {
            initializeRepository();
            loadContentModel();
        } catch (IOException | RepositoryException | ParseException e) {
            throw new InkstandRuntimeException("Could not start repository", e);
        }

    }

    @PreDestroy
    public void shutdownRepository(@Disposes Repository repository) {

        try {
            if (repository == this.repository) {
                if (adminSession != null) {
                    adminSession.logout();
                    adminSession = null;
                }
                this.repository.shutdown();
                FileUtils.deleteDirectory(tempFolder.toFile());
            }
            // TODO else throw ... what?
        } catch (final IOException e) {
            throw new InkstandRuntimeException("Could not cleanup temp folder", e);
        }
    }

    /**
     * Creates a transient test repository for integration testing
     * 
     * @throws IOException
     * @throws ConfigurationException
     */
    private void initializeRepository() throws IOException, ConfigurationException {
        this.tempFolder = Files.createTempDirectory("inque");
        final URL configLocation = getConfigURL();
        repository = JackrabbitUtil.createTransientRepository(tempFolder.toFile(), configLocation);
    }

    /**
     * Retrieves the configuration URL for the TransientRepository. The method tries to resolve the the configured
     * configURL in the classpath. If that fails, it tries to create an URL from the string directly.
     * 
     * @return the configuration URL
     * @throws MalformedURLException
     *             if the config URL is no valid URL and could not be found in the classpath
     */
    private URL getConfigURL() throws MalformedURLException {

        String strUrl = configURL;
        URL url = resolveUrl(strUrl);
        return url;
    }

    /**
     * Initializes the Test Repository with the Inque nodetype model
     * 
     * @throws RepositoryException
     * @throws IOException
     * @throws ParseException
     */
    private void loadContentModel() throws RepositoryException, IOException, ParseException {
        final Session session = getAdminSession();
        if (cndFileURL != null) {
            JackrabbitUtil.initializeContentModel(session, resolveUrl(cndFileURL));
        }
    }

    /**
     * Logs into the repository as administrator
     * 
     * @return the session with admin privileges.
     * @throws RepositoryException
     */
    private Session getAdminSession() throws RepositoryException {
        if (adminSession == null) {
            adminSession = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
        }
        return adminSession;
    }

    /**
     * Resolves the given String URL by searching the classpath using the context class loader and this class'
     * classloader. If not such resources can be found, the URL is accessed directly.
     * 
     * @param strUrl
     *            the URL as a string
     * @return the resolved URL
     * @throws MalformedURLException
     *             if the resource was not found in classpath and is not valid URL either.
     */
    private URL resolveUrl(String strUrl) throws MalformedURLException {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        URL url = null;
        if (ccl != null) {
            url = ccl.getResource(strUrl);
        }
        if (url == null) {
            url = getClass().getResource(strUrl);
        }
        if (url == null) {
            url = new URL(strUrl);
        }
        return url;
    }

}
