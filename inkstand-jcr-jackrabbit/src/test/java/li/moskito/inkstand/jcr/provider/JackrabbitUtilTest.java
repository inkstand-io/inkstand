package li.moskito.inkstand.jcr.provider;

import static io.inkstand.scribble.JCRAssert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.jackrabbit.core.TransientRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.rules.jcr.ContentRepository;
import li.moskito.inkstand.InkstandRuntimeException;

public class JackrabbitUtilTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ContentRepository repository = Scribble.newTempFolder().aroundInMemoryContentRepository().build();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

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
