package io.inkstand.jcr.util;

import static io.inkstand.scribble.JCRAssert.assertMixinNodeType;
import static io.inkstand.scribble.JCRAssert.assertNodeExistByPath;
import static io.inkstand.scribble.JCRAssert.assertPrimaryNodeType;
import static io.inkstand.scribble.JCRAssert.assertStringPropertyEquals;

import java.net.URL;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import io.inkstand.InkstandRuntimeException;
import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.rules.jcr.ContentRepository;

public class JCRContentLoaderTest {

    @ClassRule
    public static ContentRepository repository = Scribble.newTempFolder().aroundInMemoryContentRepository().build();

    private JCRContentLoader subject;

    @Before
    public void setUp() throws Exception {
        subject = new JCRContentLoader();
    }

    @Test
    public void testLoadContent_validResource() throws Exception {
        // prepare
        final URL resource = getClass().getResource("test01_inkstandJcrImport_v1-0.xml");
        final Session actSession = repository.login("admin","admin");
        // act
        subject.loadContent(actSession, resource);
        // assert
        final Session verifySession = repository.getRepository().login();
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
        final Session actSession = repository.login("admin", "admin");
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = schemaFactory.newSchema(getClass().getResource("inkstandJcrImport_v1-0.xsd"));
        // act
        subject.setSchema(schema);
        subject.loadContent(actSession, resource);
        // assert
        final Session verifySession = repository.getRepository().login();
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
        final Session actSession = repository.login("admin", "admin");
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = schemaFactory.newSchema(getClass().getResource("inkstandJcrImport_v1-0.xsd"));
        // act
        subject.setSchema(schema);
        subject.loadContent(actSession, resource);
    }
}
