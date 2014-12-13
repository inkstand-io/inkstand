package li.moskito.inkstand.jcr.util;

import static li.moskito.scribble.JCRAssert.assertMixinNodeType;
import static li.moskito.scribble.JCRAssert.assertNodeExistByPath;
import static li.moskito.scribble.JCRAssert.assertPrimaryNodeType;
import static li.moskito.scribble.JCRAssert.assertStringPropertyEquals;

import java.net.URL;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import li.moskito.inkstand.InkstandRuntimeException;
import li.moskito.scribble.ScribbleRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

public class JCRContentLoaderTest {

    @ClassRule
    public static ScribbleRule SCRIBBLE = new ScribbleRule();

    private JCRContentLoader subject;

    @Before
    public void setUp() throws Exception {
        subject = new JCRContentLoader();
    }

    @Test
    public void testLoadContent_validResource() throws Exception {
        // prepare
        final URL resource = getClass().getResource("test01_inkstandJcrImport_v1-0.xml");
        final Session actSession = SCRIBBLE.getJcrSession().getAdminSession();
        // act
        subject.loadContent(actSession, resource);
        // assert
        final Session verifySession = SCRIBBLE.getJcrSession().login();
        verifySession.refresh(true);
        assertNodeExistByPath(verifySession, "/root");
        final Node root = verifySession.getNode("/root");
        assertPrimaryNodeType(root, "nt:unstructured");
        assertMixinNodeType(root, "mix:title");
        assertStringPropertyEquals(root, "jcr:title", "TestTitle");
    }

    @Test
    public void testLoadContent_validating_validResource() throws Exception {
        // prepare
        final URL resource = getClass().getResource("test01_inkstandJcrImport_v1-0.xml");
        final Session actSession = SCRIBBLE.getJcrSession().getAdminSession();
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = schemaFactory.newSchema(getClass().getResource("inkstandJcrImport_v1-0.xsd"));
        // act
        subject.setSchema(schema);
        subject.loadContent(actSession, resource);
        // assert
        final Session verifySession = SCRIBBLE.getJcrSession().login();
        verifySession.refresh(true);
        assertNodeExistByPath(verifySession, "/root");
        final Node root = verifySession.getNode("/root");
        assertPrimaryNodeType(root, "nt:unstructured");
        assertMixinNodeType(root, "mix:title");
        assertStringPropertyEquals(root, "jcr:title", "TestTitle");
    }

    // TODO test is ignored as the JCRContentHandler does not implement an error method to react on invalid xml
    @Test(expected = InkstandRuntimeException.class)
    @Ignore
    public void testLoadContent_validating_invalidResource() throws Exception {
        // prepare
        final URL resource = getClass().getResource("test01_inkstandJcrImport_v1-0_invalid.xml");
        final Session actSession = SCRIBBLE.getJcrSession().getAdminSession();
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = schemaFactory.newSchema(getClass().getResource("inkstandJcrImport_v1-0.xsd"));
        // act
        subject.setSchema(schema);
        subject.loadContent(actSession, resource);
    }
}
