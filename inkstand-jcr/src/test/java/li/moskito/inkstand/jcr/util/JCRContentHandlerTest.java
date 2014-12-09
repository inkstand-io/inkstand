package li.moskito.inkstand.jcr.util;

import static li.moskito.scribble.JCRAssert.assertMixinNodeType;
import static li.moskito.scribble.JCRAssert.assertNodeExistByPath;
import static li.moskito.scribble.JCRAssert.assertPrimaryNodeType;
import static li.moskito.scribble.JCRAssert.assertStringPropertyEquals;
import static org.junit.Assert.assertEquals;

import javax.jcr.Node;
import javax.jcr.Session;

import li.moskito.scribble.ScribbleRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

public class JCRContentHandlerTest {

    private static final String PROPERTY = "property";
    private static final String INK_PROPERTY = "ink:property";
    private static final String MIXIN = "mixin";
    private static final String INK_MIXIN = "ink:mixin";
    private static final String INK_ROOT_NODE = "ink:rootNode";
    private static final String ROOT_NODE = "rootNode";
    @ClassRule
    public static ScribbleRule SCRIBBLE = new ScribbleRule();
    private JCRContentHandler subject;

    private static final String INKSTAND_IMPORT_NAMESPACE = "http://www.moskito.li/schemas/jcr-import";

    @Before
    public void setUp() throws Exception {
        this.subject = new JCRContentHandler(SCRIBBLE.getJcrSession().getAdminSession());
    }

    public void eventFlow_rootNodeOnly() throws Exception {
        this.subject.startDocument();
        this.subject.startPrefixMapping("ink", INKSTAND_IMPORT_NAMESPACE);

        this.subject.startElement(INKSTAND_IMPORT_NAMESPACE, ROOT_NODE, INK_ROOT_NODE,
                createAttributes("name", "root", "primaryType", "nt:unstructured"));

        this.subject.startElement(INKSTAND_IMPORT_NAMESPACE, MIXIN, INK_MIXIN, createAttributes("name", "mix:title"));
        this.subject.endElement(INKSTAND_IMPORT_NAMESPACE, MIXIN, INK_MIXIN);

        this.subject.startElement(INKSTAND_IMPORT_NAMESPACE, PROPERTY, INK_PROPERTY,
                createAttributes("name", "jcr:title", "jcrType", "STRING"));
        this.subject.characters("TestTitle".toCharArray(), 0, 9);
        this.subject.endElement(INKSTAND_IMPORT_NAMESPACE, PROPERTY, INK_PROPERTY);

        this.subject.endElement(INKSTAND_IMPORT_NAMESPACE, ROOT_NODE, INK_ROOT_NODE);
        this.subject.endPrefixMapping("ink");
        this.subject.endDocument();
    }

    private Attributes createAttributes(String... att) {
        assertEquals("list must be name-value pairs", 0, att.length % 2);
        AttributesImpl attr = new AttributesImpl();
        for (int i = 0, len = att.length; i < len; i += 2) {
            // no namespace & qname for attributes
            attr.addAttribute("", att[i], att[i], "CDATA", att[i + 1]);
        }
        return attr;
    }

    @Test
    public void testEventFlow_01() throws Exception {
        // act
        eventFlow_rootNodeOnly();
        // assert
        Session session = SCRIBBLE.getRepository().login("admin", "admin");
        assertNodeExistByPath(session, "/root");
        Node rootNode = session.getNode("/root");
        assertPrimaryNodeType(rootNode, "nt:unstructured");
        assertMixinNodeType(rootNode, "mix:title");
        assertStringPropertyEquals(rootNode, "jcr:title", "TestTitle");
    }
}
