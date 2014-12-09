package li.moskito.inkstand.jcr.util;

import static li.moskito.scribble.JCRAssert.assertMixinNodeType;
import static li.moskito.scribble.JCRAssert.assertNodeExistByPath;
import static li.moskito.scribble.JCRAssert.assertPrimaryNodeType;
import static li.moskito.scribble.JCRAssert.assertStringPropertyEquals;

import java.net.URL;

import javax.jcr.Node;
import javax.jcr.Session;

import li.moskito.scribble.ScribbleRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class JCRContentLoaderTest {

    @ClassRule
    public static ScribbleRule SCRIBBLE = new ScribbleRule();

    private JCRContentLoader subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new JCRContentLoader();
    }

    @Test
    public void testLoadContent_validResource() throws Exception {
        // prepare
        URL resource = getClass().getResource("test01_inkstandJcrImport_v1-0.xml");
        Session actSession = SCRIBBLE.getJcrSession().getAdminSession();
        // act
        this.subject.loadContent(actSession, resource);
        // assert
        Session verifySession = SCRIBBLE.getJcrSession().login();
        assertNodeExistByPath(verifySession, "/root");
        Node root = verifySession.getNode("/root");
        assertPrimaryNodeType(root, "nt:unstructured");
        assertMixinNodeType(root, "mix:title");
        assertStringPropertyEquals(root, "jcr:title", "TestTitle");
        verifySession.logout();

    }

}
