package li.moskito.inkstand.jcr.util;

import static li.moskito.scribble.JCRAssert.assertNodeExist;
import static li.moskito.scribble.JCRAssert.assertStringPropertyEquals;

import java.net.URL;

import javax.jcr.Node;
import javax.jcr.Session;

import li.moskito.scribble.ContentRepository;
import li.moskito.scribble.InMemoryContentRepository;
import li.moskito.scribble.JCRSession;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;

public class JCRContentLoader2Test {

    private static TemporaryFolder tempFolder = new TemporaryFolder();
    private static ContentRepository repository = new InMemoryContentRepository(tempFolder);
    private static JCRSession jcrSession = new JCRSession(repository);
    @ClassRule
    public static RuleChain chain = RuleChain.outerRule(tempFolder).around(repository).around(jcrSession);

    private JCRContentLoader2 subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new JCRContentLoader2();
    }

    @Test
    public void testLoadContent_validResource() throws Exception {
        // prepare
        URL resource = getClass().getResource("test01_inkstandJcrImport_v1-0.xml");
        Session actSession = jcrSession.getAdminSession();
        // act
        this.subject.loadContent(actSession, resource);
        // assert

        Session verifySession = jcrSession.login();
        assertNodeExist(verifySession, "/root");
        Node root = verifySession.getNode("/root");
        assertStringPropertyEquals(root, "jcr:title", "TestTitle");
        verifySession.logout();

    }

}
