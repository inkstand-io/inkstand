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

import static io.inkstand.scribble.JCRAssert.assertNodeTypeExists;
import static io.inkstand.scribble.Scribble.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.jcr.Repository;
import javax.jcr.Session;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.fs.mem.MemoryFileSystem;
import org.apache.jackrabbit.core.persistence.mem.InMemBundlePersistenceManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.scribble.Scribble;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryRepositoryProviderTest {

    @Rule
    public TemporaryFolder folder = Scribble.newTempFolder().build();

    @InjectMocks
    private InMemoryRepositoryProvider subject;

    private Repository repository;

    private ClassLoader contextClassLoader;

    @Before
    public void setUp() throws Exception {

        contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @After
    public void tearDown() throws Exception {

        Thread.currentThread().setContextClassLoader(contextClassLoader);
        if(repository != null && repository instanceof RepositoryImpl) {
            ((RepositoryImpl)repository).shutdown();
        }
    }

    private String getResourceUrl(final String resourceName) {

        return getClass().getResource(resourceName).toString();
    }

    @Test
    public void testGetRepositoryHome_preConfigured() throws Exception {
        //prepare
        Path homepath = folder.getRoot().toPath();
        inject(homepath.toString()).asConfigProperty("inkstand.jcr.home").into(subject);

        //act
        String actual = subject.getRepositoryHome();

        //assert
        assertEquals(homepath.toString(), actual);

    }

    @Test
    public void testGetRepositoryHome_tempFolder() throws Exception {
        //prepare

        //act
        String actual = subject.getRepositoryHome();

        //assert
        assertNotNull(actual);
        assertTrue(Files.exists(Paths.get(actual)));
    }

    @Test
    public void testGetRepositoryHome_tempFolderReuse() throws Exception {
        //prepare

        //act
        String initialFolder = subject.getRepositoryHome();
        String nextFolder = subject.getRepositoryHome();

        //assert
        assertNotNull(initialFolder);
        assertEquals(initialFolder, nextFolder);
    }

    @Test
    public void testGetRepositoryConfig_defaultConfigXml() throws Exception {
        //prepare
        //null will result in the default value being injected
        inject(null).asConfigProperty("inkstand.jcr.config").into(subject);
        Thread.currentThread().setContextClassLoader(null);

        //act
        RepositoryConfig config = subject.getRepositoryConfig();
        //assert
        assertNotNull(config);
        assertEquals(config.getHomeDir(), subject.getRepositoryHome());
        assertTrue(config.getFileSystem() instanceof MemoryFileSystem);
        assertEquals(InMemBundlePersistenceManager.class.getName(),
                     config.getWorkspaceConfig("default").getPersistenceManagerConfig().getClassName());
    }

    @Test
    public void testGetRepositoryConfig_externalConfigXml() throws Exception {
        //prepare
        inject(getResourceUrl("InMemoryRepositoryProviderTest_externalConfig.xml")).asConfigProperty(
                "inkstand.jcr.config").into(subject);
        Thread.currentThread().setContextClassLoader(null);

        //act
        RepositoryConfig config = subject.getRepositoryConfig();
        //assert
        assertNotNull(config);
        assertEquals(config.getHomeDir(), subject.getRepositoryHome());
        assertTrue(config.getFileSystem() instanceof MemoryFileSystem);
        assertEquals(InMemBundlePersistenceManager.class.getName(),
                     config.getWorkspaceConfig("default").getPersistenceManagerConfig().getClassName());

    }

    @Test(expected = ConfigurationException.class)
    public void testGetRepositoryConfig_invalidConfigXml() throws Exception {
        //prepare
        inject(getResourceUrl("InMemoryRepositoryProviderTest_invalid_repository.xml")).asConfigProperty(
                "inkstand.jcr.config").into(subject);

        //act
        subject.getRepositoryConfig();
    }

    @Test(expected = ConfigurationException.class)
    public void testGetRepositoryConfig_brokenConfigXml() throws Exception {
        //prepare
        inject(getResourceUrl("InMemoryRepositoryProviderTest_broken_repository.xml")).asConfigProperty(
                "inkstand.jcr.config").into(subject);

        //act
        RepositoryConfig config = subject.getRepositoryConfig();
        //assert
        //the class is only instantiated on demand, so this call will fail
        assertNotNull(config.getFileSystem());
    }

    @Test
    public void testGetRepository_withCnd() throws Exception {

        //prepare
        inject(null).asConfigProperty("inkstand.jcr.config").into(subject);
        inject(getResourceUrl("InMemoryRepositoryProviderTest_ntmodel.cnd"))
                .asConfigProperty("inkstand.jcr.cnd")
                .into(subject);

        //act
        repository = subject.getRepository();

        //assert
        assertNotNull(repository);
        Session session = repository.login();
        assertNodeTypeExists(session, "test:testType");

    }

    @Test
    public void testGetRepository_withoutCnd() throws Exception {
        //prepare
        inject(null).asConfigProperty("inkstand.jcr.config").into(subject);

        //act
        repository = subject.getRepository();

        //assert
        assertNotNull(repository);
    }

    @Test
    public void testClose_configuredWorkingDirectory_noCleanup() throws Exception {
        //prepare
        //injecting null to activate the default config
        inject(null).asConfigProperty("inkstand.jcr.config").into(subject);
        Path workDir = folder.getRoot().toPath();
        inject(workDir.toString()).asConfigProperty("inkstand.jcr.home").into(subject);
        Repository repository = subject.getRepository();

        //act
        subject.close(repository);

        //assert
        assertTrue(Files.exists(workDir));
    }

    @Test
    public void testClose_tempWorkingDirectory_cleanup() throws Exception {
        //prepare
        //injecting null to activate the default config
        inject(null).asConfigProperty("inkstand.jcr.config").into(subject);
        //no workdir -> use temp folder
        repository = subject.getRepository();

        //act
        subject.close(repository);

        //assert
        assertFalse("Temporary folder was not deleted", Files.exists(Paths.get(subject.getRepositoryHome())));
    }
}
