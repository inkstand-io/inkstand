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

import static io.inkstand.jcr.provider.JackrabbitUtil.asTransientRepository;

import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import java.io.File;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.inkstand.jcr.RepositoryProvider;
import io.inkstand.jcr.StandaloneRepository;

/**
 * Provider for a JCR {@link Repository} that uses a local repository configuration. The repository home directory is
 * configured using the {@code inkstand.jcr.home} property, either defined in the inkstand.properties file or via JVM
 * argument.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@Priority(2)
public class StandaloneRepositoryProvider implements RepositoryProvider {

    private static final Logger LOG = LoggerFactory.getLogger(StandaloneRepositoryProvider.class);

    @Inject
    @ConfigProperty(name = "inkstand.jcr.home")
    private String repositoryHome;

    @Inject
    @ConfigProperty(name = "inkstand.jcr.config")
    private String configFile;

    @Inject
    @ConfigProperty(name = "inkstand.jcr.transient", defaultValue = "false")
    private Boolean transientRepository;

    private Repository repository;

    @Override
    @Produces
    @StandaloneRepository
    public Repository getRepository() throws RepositoryException {
        if (repository == null) {
            final RepositoryImpl newRepository = createRepository();
            if(isTransient()){
                repository = asTransientRepository(newRepository);
            } else {
                repository = newRepository;
            }
        }
        return repository;
    }

    private Boolean isTransient() {

        return transientRepository != null && transientRepository.booleanValue();
    }

    @PreDestroy
    public void close(@Disposes final Repository repository) {
        if (repository instanceof RepositoryImpl) {
            ((RepositoryImpl) repository).shutdown();
        }
    }

    /**
     * Creates the repository instance. The default uses the config location and the working directory location
     * and create a standalone instance from both. If the transient flag is set, a transient repository wrapper
     * is applied making the repository transient.
     * @return
     *  the repository instance to use
     * @throws RepositoryException
     *  if the creation of the repository failed. Reasons could be an invalid or missing configuration file.
     */
    protected RepositoryImpl createRepository() throws RepositoryException {

        final RepositoryConfig config = getRepositoryConfig();
        LOG.info("Connecting to local repository at {}", config.getHomeDir());
        return RepositoryImpl.create(config);
    }

    /**
     * Retrieves the configuration for the repository. The configuration is loaded from the config file defined
     * by the {@code inkstand.jcr.config} file and {@code inkstand.jcr.home} for the working directory.
     * Override to create an alternative configuration.
     * @return
     *  a configuration for the standalone repository
     * @throws ConfigurationException
     *  if the configuration can not be instantiated
     */
    protected RepositoryConfig getRepositoryConfig() throws
            ConfigurationException {
        return RepositoryConfig.create(new File(configFile), new File(getRepositoryHome()));
    }

    /**
     * The location of the working directory
     * @return
     *  the path to the working directory
     */
    public String getRepositoryHome() {

        return repositoryHome;
    }
}
