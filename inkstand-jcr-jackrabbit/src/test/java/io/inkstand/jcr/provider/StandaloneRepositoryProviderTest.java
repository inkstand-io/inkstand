/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.jcr.provider;

import static io.inkstand.scribble.Scribble.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.jcr.Repository;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.rules.TemporaryFile;

public class StandaloneRepositoryProviderTest {

    @Rule
    public final TemporaryFile file = Scribble.newTempFolder().aroundTempFile("repository.xml").fromResource(getClass().getResource(
            "StandaloneRepositoryProviderTest_config.xml")).build();

    private StandaloneRepositoryProvider subject;
    private Repository repository;

    @Before
    public void setUp() throws Exception {
        subject = new StandaloneRepositoryProvider();
        inject(file.getFile().getParentFile().getAbsolutePath()).asConfigProperty("inkstand.jcr.home").into(subject);
        inject(file.getFile().getAbsolutePath()).asConfigProperty("inkstand.jcr.config").into(subject);
    }

    @After
    public void tearDown() throws Exception {
        if (repository != null) {
            if(repository instanceof RepositoryImpl) {
                ((RepositoryImpl) repository).shutdown();
            } else if(repository instanceof TransientRepository){
                ((TransientRepository)repository).shutdown();
            }
        }
    }

    @Test
    public void testGetRepository_TransientFlagDefaultFalse_noTransientRepository() throws Exception {
        repository = subject.getRepository();
        assertNotNull(repository);
        assertFalse(repository instanceof TransientRepository);
    }
    @Test
    public void testGetRepository_TransientFlagFalse_noTransientRepository() throws Exception {
        //prepare
        inject(Boolean.FALSE).asConfigProperty("inkstand.jcr.transient").into(subject);

        //act
        repository = subject.getRepository();

        //assert
        assertNotNull(repository);
        assertFalse(repository instanceof TransientRepository);
    }

    @Test
    public void testGetRepository_TransientFlagTrue_transientRepository() throws Exception {
        //prepare
        inject(Boolean.TRUE).asConfigProperty("inkstand.jcr.transient").into(subject);

        //act
        this.repository = subject.getRepository();

        //assert
        assertTrue(repository instanceof TransientRepository);


    }

    @Test
    public void testClose_RepositoryImpl_shutdown() throws Exception {
        final RepositoryImpl repo = mock(RepositoryImpl.class);
        subject.close(repo);
        verify(repo).shutdown();
    }

    @Test
    public void testClose_NoRepositoryImpl_noShutdown() throws Exception {
        final org.apache.jackrabbit.core.TransientRepository repo = mock(org.apache.jackrabbit.core.TransientRepository.class);
        subject.close(repo);
        verify(repo, times(0)).shutdown();
    }

    @Test
    public void testGetRepositoryHome() throws Exception {
        //prepare
        inject("testHome").asConfigProperty("inkstand.jcr.home").into(subject);

        //act
        String actualHome = subject.getRepositoryHome();

        //assert
        assertEquals("testHome", actualHome);
    }

    @Test
    public void testGetRepositoryConfig_validConfig() throws Exception {
        //prepare
        //see setUp

        //act
        RepositoryConfig config = subject.getRepositoryConfig();

        //assert
        assertNotNull(config);
        assertEquals(file.getFile().getParentFile().getAbsolutePath(), config.getHomeDir());
    }

    @Test
    public void testCreateRepository() throws Exception {
        //prepare

        //act
        RepositoryImpl repository = subject.createRepository();

        //assert
        assertNotNull(repository);
        assertEquals(file.getFile().getParentFile().getAbsolutePath(), repository.getConfig().getHomeDir());

    }
}
