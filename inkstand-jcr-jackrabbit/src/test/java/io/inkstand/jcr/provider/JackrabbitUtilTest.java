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

import static io.inkstand.scribble.JCRAssert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.jackrabbit.core.TransientRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.rules.jcr.ContentRepository;
import io.inkstand.InkstandRuntimeException;

public class JackrabbitUtilTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public final ContentRepository repository = Scribble.newTempFolder().aroundInMemoryContentRepository().build();


    @Test
    public void testCreateTransientRepository() throws Exception {
        final TransientRepository repo = JackrabbitUtil.createTransientRepository(folder.getRoot(), getClass()
                .getResource("JackrabbitUtilTest_testCreateTransientRepository.xml"));
        assertNotNull(repo);
        assertEquals(folder.getRoot().toString(), repo.getHomeDir());
        repo.shutdown();
    }

    @Test(expected = InkstandRuntimeException.class)
    public void testCreateTransientRepository_invalidUrl() throws Exception {
        final TransientRepository repo = JackrabbitUtil.createTransientRepository(folder.getRoot(), new URL(
                "http://localhost/someMissingFile.txt"));
        assertNotNull(repo);
        assertEquals(folder.getRoot().toString(), repo.getHomeDir());
        repo.shutdown();
    }

    @Test
    public void testInitializeContentModel() throws Exception {
        final Session session = repository.login("admin","admin");
        JackrabbitUtil.initializeContentModel(session,
                getClass().getResource("JackrabbitUtilTest_testInitializeContentModel.cnd"));

        assertNodeTypeExists(session, "test:testType");
    }

    @Test(expected = InkstandRuntimeException.class)
    public void testInitializeContentModel_invalidUrl() throws Exception {
        final Session session = repository.login("admin", "admin");
        JackrabbitUtil.initializeContentModel(session, new URL("http://localhost/someMissingFile.txt"));

        assertNodeTypeExists(session, "test:testType");
    }

    @Test(expected = InkstandRuntimeException.class)
    public void testInitializeContentModel_invalidCnd() throws Exception {
        final Session session = repository.login("admin", "admin");
        JackrabbitUtil.initializeContentModel(session,
                getClass().getResource("JackrabbitUtilTest_testInitializeContentModel_invalid.cnd"));

        assertNodeTypeExists(session, "test:testType");
    }

    @Test
    public void testLoadContent() throws Exception {
        final Session session = repository.login("admin","admin");
        JackrabbitUtil.loadContent(session, getClass().getResource("JackrabbitUtilTest_testLoadContent.xml"));

        assertNodeExistByPath(session, "/root");
        final Node node = session.getNode("/root");
        assertPrimaryNodeType(node, "nt:unstructured");
        assertMixinNodeType(node, "mix:title");
        assertStringPropertyEquals(node, "jcr:title", "TestTitle");
    }

}
