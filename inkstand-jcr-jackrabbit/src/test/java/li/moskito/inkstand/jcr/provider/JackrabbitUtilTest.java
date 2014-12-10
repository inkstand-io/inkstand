package li.moskito.inkstand.jcr.provider;

import static li.moskito.scribble.JCRAssert.assertMixinNodeType;
import static li.moskito.scribble.JCRAssert.assertNodeExistByPath;
import static li.moskito.scribble.JCRAssert.assertPrimaryNodeType;
import static li.moskito.scribble.JCRAssert.assertStringPropertyEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeTypeManager;

import li.moskito.scribble.ScribbleRule;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JackrabbitUtilTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ScribbleRule repository = new ScribbleRule();

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

    @Test
    public void testInitializeContentModel() throws Exception {
        final Session session = repository.getJcrSession().getAdminSession();
        JackrabbitUtil.initializeContentModel(session,
                getClass().getResource("JackrabbitUtilTest_testInitializeContentModel.cnd"));

        final NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
        assertNotNull(ntm.getNodeType("test:testType"));
    }

    @Test
    public void testLoadContent() throws Exception {
        final Session session = repository.getJcrSession().getAdminSession();
        JackrabbitUtil.loadContent(session, getClass().getResource("JackrabbitUtilTest_testLoadContent.xml"));

        assertNodeExistByPath(session, "/root");
        final Node node = session.getNode("/root");
        assertPrimaryNodeType(node, "nt:unstructured");
        assertMixinNodeType(node, "mix:title");
        assertStringPropertyEquals(node, "jcr:title", "TestTitle");
    }

}
