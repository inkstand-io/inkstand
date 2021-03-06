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

package io.inkstand.jcr.util;

import static io.inkstand.scribble.jcr.JCRAssert.*;
import static org.junit.Assert.assertEquals;

import javax.jcr.Node;
import javax.jcr.Session;

import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.jcr.rules.ContentRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class JCRContentHandlerTest {

    private static final String INK_NODE = "ink:node";
    private static final String NODE = "node";
    private static final String PROPERTY = "property";
    private static final String INK_PROPERTY = "ink:property";
    private static final String MIXIN = "mixin";
    private static final String INK_MIXIN = "ink:mixin";
    private static final String INK_ROOT_NODE = "ink:rootNode";
    private static final String ROOT_NODE = "rootNode";
    @Rule
    public final ContentRepository repository = Scribble.newTempFolder().aroundInMemoryContentRepository().build();
    private Session adminSession = null;
    private JCRContentHandler subject;

    private static final String INKSTAND_IMPORT_NAMESPACE = "http://inkstand.io/schemas/jcr-import";

    @Before
    public void setUp() throws Exception {
        adminSession = repository.login("admin", "admin");
        subject = new JCRContentHandler(adminSession);
    }

    @After
    public void tearDown() throws Exception {
        if(adminSession != null) {
            adminSession.logout();
        }
    }

    /**
     * A simple flow of events that creates an unstructured root node
     *
     * @param namespace
     *            the namespace for all element events
     * @throws SAXException
     */
    protected void ns_eventflow_rootNode(final String namespace) throws SAXException {
        subject.startDocument();
        subject.startPrefixMapping("ink", namespace);

        subject.startElement(namespace, ROOT_NODE, INK_ROOT_NODE,
                createAttributes("name", "root", "primaryType", "nt:unstructured"));

        subject.startElement(namespace, MIXIN, INK_MIXIN, createAttributes("name", "mix:title"));
        subject.endElement(namespace, MIXIN, INK_MIXIN);

        subject.startElement(namespace, PROPERTY, INK_PROPERTY,
                createAttributes("name", "jcr:title", "jcrType", "STRING"));
        subject.characters("TestTitle".toCharArray(), 0, 9);
        subject.endElement(namespace, PROPERTY, INK_PROPERTY);

        subject.endElement(namespace, ROOT_NODE, INK_ROOT_NODE);
        subject.endPrefixMapping("ink");
        subject.endDocument();
    }

    /**
     * A simple flow of events that creates an unstructured root node with one child node
     *
     * @param namespace
     *            the namespace for all element events
     * @throws SAXException
     */
    protected void ns_eventflow_rootAndChildNode(final String namespace) throws SAXException {
        subject.startDocument();
        subject.startPrefixMapping("ink", namespace);

        subject.startElement(namespace, ROOT_NODE, INK_ROOT_NODE,
                createAttributes("name", "root", "primaryType", "nt:unstructured"));

        subject.startElement(namespace, MIXIN, INK_MIXIN, createAttributes("name", "mix:title"));
        subject.endElement(namespace, MIXIN, INK_MIXIN);

        subject.startElement(namespace, PROPERTY, INK_PROPERTY,
                createAttributes("name", "jcr:title", "jcrType", "STRING"));
        subject.characters("TestTitle".toCharArray(), 0, 9);
        subject.endElement(namespace, PROPERTY, INK_PROPERTY);

        // start child node
        subject.startElement(namespace, NODE, INK_NODE,
                createAttributes("name", "child", "primaryType", "nt:unstructured"));
        subject.startElement(namespace, MIXIN, INK_MIXIN, createAttributes("name", "mix:title"));
        subject.endElement(namespace, MIXIN, INK_MIXIN);
        subject.startElement(namespace, PROPERTY, INK_PROPERTY,
                createAttributes("name", "jcr:title", "jcrType", "STRING"));
        subject.characters("ChildNode".toCharArray(), 0, 9);
        subject.endElement(namespace, PROPERTY, INK_PROPERTY);
        subject.endElement(namespace, NODE, INK_NODE);
        // end child node

        subject.endElement(namespace, ROOT_NODE, INK_ROOT_NODE);
        subject.endPrefixMapping("ink");
        subject.endDocument();
    }

    protected void ns_eventFlow_ignoredElements() throws SAXException {
        final String namespace = INKSTAND_IMPORT_NAMESPACE;
        subject.startDocument();
        subject.startPrefixMapping("ink", namespace);

        subject.startElement(namespace, "ignore", "ink:ignore",
                createAttributes("name", "root", "primaryType", "nt:unstructured"));

        subject.startElement(namespace, "ignoreMixin", "ink:ignoreMixin", createAttributes("name", "mix:title"));
        subject.endElement(namespace, "ignoreMixin", "ink:ignoreMixin");

        subject.startElement(namespace, "ignoreProperty", "ink:ignoreProperty",
                createAttributes("name", "jcr:title", "jcrType", "STRING"));
        subject.characters("TestTitle".toCharArray(), 0, 9);
        subject.endElement(namespace, "ignoreProperty", "ink:ignoreProperty");

        subject.endElement(namespace, "ignore", "ink:ignore");
        subject.endPrefixMapping("ink");
        subject.endDocument();
    }

    /**
     * A simple flow of events that creates an unstructured root node
     *
     * @throws Exception
     */
    public void eventFlow_ignoredNameSpace() throws Exception {
        subject.startDocument();
        subject.startPrefixMapping("ink", "");

        subject.startElement(INKSTAND_IMPORT_NAMESPACE, ROOT_NODE, INK_ROOT_NODE,
                createAttributes("name", "root", "primaryType", "nt:unstructured"));

        subject.startElement(INKSTAND_IMPORT_NAMESPACE, MIXIN, INK_MIXIN, createAttributes("name", "mix:title"));
        subject.endElement(INKSTAND_IMPORT_NAMESPACE, MIXIN, INK_MIXIN);

        subject.startElement(INKSTAND_IMPORT_NAMESPACE, PROPERTY, INK_PROPERTY,
                createAttributes("name", "jcr:title", "jcrType", "STRING"));
        subject.characters("TestTitle".toCharArray(), 0, 9);
        subject.endElement(INKSTAND_IMPORT_NAMESPACE, PROPERTY, INK_PROPERTY);

        subject.endElement(INKSTAND_IMPORT_NAMESPACE, ROOT_NODE, INK_ROOT_NODE);
        subject.endPrefixMapping("ink");
        subject.endDocument();
    }

    private Attributes createAttributes(final String... att) {
        assertEquals("list must be name-value pairs", 0, att.length % 2);
        final AttributesImpl attr = new AttributesImpl();
        for (int i = 0, len = att.length; i < len; i += 2) {
            // no namespace & qname for attributes
            attr.addAttribute("", att[i], att[i], "CDATA", att[i + 1]);
        }
        return attr;
    }

    @Test
    public void testEventFlow_01_simpleStructure() throws Exception {
        // act
        ns_eventflow_rootNode(INKSTAND_IMPORT_NAMESPACE);
        // assert
        final Session session = repository.login("admin", "admin");
        assertNodeExistByPath(session, "/root");
        final Node rootNode = session.getNode("/root");
        assertPrimaryNodeType(rootNode, "nt:unstructured");
        assertMixinNodeType(rootNode, "mix:title");
        assertStringPropertyEquals(rootNode, "jcr:title", "TestTitle");
    }

    @Test
    public void testEventFlow_01_ignoredNamespace() throws Exception {
        // act
        ns_eventflow_rootNode("ignore");
        // assert
        final Session session = repository.login("admin", "admin");
        // nothing is created at all
        assertNodeNotExistByPath(session, "/root");
    }

    @Test
    public void testEventFlow_02_ignoredElements() throws Exception {
        // act
        ns_eventFlow_ignoredElements();
        // assert
        final Session session = repository.login("admin", "admin");
        // nothing is created at all
        assertNodeNotExistByPath(session, "/root");
    }

    @Test
    public void testEventFlow_03_simpleNestedStructure() throws Exception {
        // act
        ns_eventflow_rootAndChildNode(INKSTAND_IMPORT_NAMESPACE);
        // assert
        final Session session = repository.login("admin", "admin");
        assertNodeExistByPath(session, "/root");
        assertNodeExistByPath(session, "/root/child");
        final Node rootNode = session.getNode("/root");
        assertPrimaryNodeType(rootNode, "nt:unstructured");
        assertMixinNodeType(rootNode, "mix:title");
        assertStringPropertyEquals(rootNode, "jcr:title", "TestTitle");

        final Node childNode = session.getNode("/root/child");
        assertPrimaryNodeType(childNode, "nt:unstructured");
        assertMixinNodeType(childNode, "mix:title");
        assertStringPropertyEquals(childNode, "jcr:title", "ChildNode");
    }

}
