/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.jcr.provider;

import javax.annotation.Priority;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;

import io.inkstand.InkstandRuntimeException;
import io.inkstand.jcr.InMemoryRepository;

/**
 * Provider for an in-memory repository. The in-memory repository uses a predefined default configuration with
 * in-memory persistence only. This config file can be overridden using the {@code inkstand.jcr.config} property making
 * this repository provider functionally identical to the {@link StandaloneRepositoryProvider} from which in inherits.
 * On top of the {@link StandaloneRepositoryProvider}
 * this implementation creates it's working directory automatically as temporary folder - which is not recommended
 * for repositories with persistence. And it provides the option to initialize the repository with a node type model
 * which has to be done manually for the standalone repository as it is typically only required once.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Priority(1)
public class InMemoryRepositoryProvider extends StandaloneRepositoryProvider  {

    /**
     * Path to the working directory. The path is kept to clean it up on shutdown.
     */
    private Path tempFolder;

    @Inject
    @ConfigProperty(name = "inkstand.jcr.config", defaultValue = "defaultInMemoryRepository.xml")
    private String configURL;

    @Inject
    @ConfigProperty(name = "inkstand.jcr.cnd")
    private String cndFileURL;

    /**
     * Admin session that is required to initialize the content model. The session is kept open so that a transient
     * repository is not shutdown right after the initialization.
     */
    private Session adminSession;

    @Override
    @Produces
    @InMemoryRepository
    public Repository getRepository() throws RepositoryException {
        final Repository repository = super.getRepository();
        try {
            initializeRepository(repository);
        } catch (IOException e) {
            throw new InkstandRuntimeException("Could not initialize repository", e);
        }
        return repository;
    }

    @Override
    public void close(@Disposes final Repository repository) {

        super.close(repository);
        if(super.getRepositoryHome() == null) {
            try {
                //only delete the folder if it has not been pre-configured
                FileUtils.deleteDirectory(tempFolder.toFile());
            } catch (IOException e) {
                throw new InkstandRuntimeException("Could not cleanup temp folder", e);
            }
        }
    }


    /**
     * Initializes the repository. This includes
     * <ul>
     *     <li>Node Types from a CND file</li>
     * </ul>
     *
     * @param repository
     *  the repository to initialize
     *
     * @throws IOException
     * @throws RepositoryException
     */
    private void initializeRepository(final Repository repository) throws IOException, RepositoryException {
        loadContentModel(repository);
    }

    @Override
    protected RepositoryConfig getRepositoryConfig() throws ConfigurationException {

        try {
            final URL configLocation = getConfigURL();
            return RepositoryConfig.create(configLocation.toURI(), getRepositoryHome());
        } catch (MalformedURLException | URISyntaxException e) {
            throw new ConfigurationException("Invalid config location", e);
        }
    }

    @Override
    public String getRepositoryHome() {

        if(tempFolder == null){
            tempFolder = initWorkingDirectory();
        }
        return tempFolder.toString();
    }

    /**
     * Initializes the working directory path by either resolving a configured working directory or by
     * creating a temporary folder.
     * @return
     *  the path to the working directory
     */
    private Path initWorkingDirectory() {

        final String configuredHome = super.getRepositoryHome();
        final Path workingDir;
        if(configuredHome != null) {
            workingDir = Paths.get(configuredHome);
        } else {
            try {
                workingDir = Files.createTempDirectory("inkstand");
            } catch (IOException e) {
                throw new InkstandRuntimeException("Could not create temporary working directory", e);
            }
        }
        return workingDir.toAbsolutePath();
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
        return resolveUrl(configURL);
    }

    /**
     * Initializes the Repository with the configured node types
     *
     * @param repository
     *  the repository into which the nodetypes should be loaded
     * @throws RepositoryException
     * @throws IOException
     */
    private void loadContentModel(final Repository repository) throws RepositoryException, IOException {
        final Session session = getAdminSession(repository);
        if (cndFileURL != null) {
            JackrabbitUtil.initializeContentModel(session, resolveUrl(cndFileURL));
        }
    }

    /**
     * Logs into the repository as administrator
     *
     * @param repository
     *  the repository to log into as admin
     * @return the session with admin privileges.
     * @throws RepositoryException
     */
    private Session getAdminSession(Repository repository) throws RepositoryException {
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
    private URL resolveUrl(final String strUrl) throws MalformedURLException {
        final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
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
