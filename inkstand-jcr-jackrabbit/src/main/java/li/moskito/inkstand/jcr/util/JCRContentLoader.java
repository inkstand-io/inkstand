package li.moskito.inkstand.jcr.util;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import edu.emory.mathcs.backport.java.util.Collections;

class JCRContentLoader {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JCRContentLoader.class);

    private static final String IRM_CONTENT_LIST_PROPERTY = "irm:contentList";
    private static final String IRM_CONTENT = "irm:Content";

    private final DocumentBuilder domBuilder;
    private final XPath xp;

    private final Map<String, List<Node>> referenceMap = new Hashtable<>();

    public JCRContentLoader() throws ParserConfigurationException {

        domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xp = XPathFactory.newInstance().newXPath();
        xp.setNamespaceContext(new NamespaceContext() {

            @Override
            public String getNamespaceURI(final String arg0) {
                return "";
            }

            @Override
            public String getPrefix(final String arg0) {
                return "";
            }

            @Override
            public Iterator<?> getPrefixes(final String arg0) {
                return Collections.emptyList().iterator();
            }
        });
    }

    public List<Node> initializeContent(final Session session, final InputStream is)
            throws Exception {

        final Document contentDOM = domBuilder.parse(is);
        final org.w3c.dom.Node domRoot = (org.w3c.dom.Node) xp.evaluate("//inboxContentDefinition", contentDOM,
                XPathConstants.NODE);
        final Node jcrRoot = session.getRootNode();
        final ValueFactory valueFactory = session.getValueFactory();

        final List<Node> contentStructure = new ArrayList<>();

        contentStructure.addAll(addNode(jcrRoot, domRoot, valueFactory));

        LOG.info("Persisting Changes");
        session.save();
        LOG.info("Content initialized");

        return contentStructure;
    }

    private Collection<? extends Node> addNode(
            final Node parent,
            final org.w3c.dom.Node contentDefinition,
            final ValueFactory factory)
            throws XPathExpressionException, RepositoryException, ItemExistsException, PathNotFoundException,
            NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, ParseException {

        final List<Node> result = new ArrayList<>();

        final String name = getNameAttribute(contentDefinition);
        final String type = getTypeAttribute(contentDefinition);
        final String create = getCreateAttribute(contentDefinition);

        // For irm:contentList, create just a new property
        if (IRM_CONTENT_LIST_PROPERTY.equals(name)) {
            parent.setProperty(name, new String[] {});
            registerReferences(parent, contentDefinition);
            return result;
        }
        // Create a new child node
        final Node newChild;
        if (!"auto".equals(create) && !parent.hasNode(name)) {
            newChild = parent.addNode(name, type);
            LOG.info("Added node {} (id={})", newChild.getPath(), newChild.getIdentifier());
        } else {
            newChild = parent.getNode(name);
            LOG.info("Node already present: {} (id={})", newChild.getPath(), newChild.getIdentifier());
        }
        result.add(newChild);
        setProperties(newChild, contentDefinition);
        setBinaryContent(newChild, contentDefinition, factory);

        result.addAll(addChildNodes(newChild, contentDefinition, factory));

        return result;
    }

    private Collection<? extends Node> addChildNodes(
            final Node parent,
            final org.w3c.dom.Node contentDefinition,
            final ValueFactory factory)
            throws XPathExpressionException, RepositoryException, ParseException {

        final List<Node> result = new ArrayList<>();

        final org.w3c.dom.NodeList childDefs = (org.w3c.dom.NodeList) xp.evaluate("*[not(self::property)]",
                contentDefinition, XPathConstants.NODESET);
        for (int i = 0; i < childDefs.getLength(); i++) {
            result.addAll(addNode(parent, childDefs.item(i), factory));
        }
        return result;
    }

    private void setProperties(final Node currentNode, final org.w3c.dom.Node contentDefinition)
            throws XPathExpressionException, ParseException, ValueFormatException, VersionException, LockException,
            ConstraintViolationException, RepositoryException {
        final org.w3c.dom.NodeList propDefs = (org.w3c.dom.NodeList) xp.evaluate("property", contentDefinition,
                XPathConstants.NODESET);
        for (int i = 0; i < propDefs.getLength(); i++) {
            final org.w3c.dom.Node propDef = propDefs.item(i);
            final String propName = getNameAttribute(propDef);
            final String strValue = getText(propDef);

            // ignore "ldr:*"
            if (propName.startsWith("ldr:")) {
                LOG.info("Ignored property {}", propName);
            } else {
                // Create new property
                final String stringToDate = getAttribute(propDef, "stringToDate");
                if (stringToDate != null && !stringToDate.isEmpty()) {
                    final SimpleDateFormat format = new SimpleDateFormat(stringToDate);
                    final Calendar cal = Calendar.getInstance();
                    cal.setTime(format.parse(strValue));
                    currentNode.setProperty(propName, cal);
                } else {
                    currentNode.setProperty(propName, strValue);
                }
                LOG.info("set property {}={} on node {}", propName, strValue, currentNode.getPath());
            }
        }
    }

    private void setBinaryContent(
            final Node current,
            final org.w3c.dom.Node contentDefinition,
            final ValueFactory factory)
            throws XPathExpressionException, ParseException, ValueFormatException, VersionException, LockException,
            ConstraintViolationException, RepositoryException {
        final org.w3c.dom.NodeList propDefs = (org.w3c.dom.NodeList) xp.evaluate("property", contentDefinition,
                XPathConstants.NODESET);
        // Only for "irm:Content" nodes
        if (!current.isNodeType(IRM_CONTENT)) {
            return;
        }

        // Add file content
        String key = null;
        for (int i = 0; i < propDefs.getLength(); i++) {
            final org.w3c.dom.Node propDef = propDefs.item(i);
            final String propName = getNameAttribute(propDef);
            final String strValue = getText(propDef);

            // Load file
            if ("ldr:path".equals(propName)) {
                final InputStream fileStream = getInputStreamFromURI(strValue);
                final Binary content = factory.createBinary(fileStream);

                Node contentNode = null;
                if (current.hasNode("jcr:content")) {
                    contentNode = current.getNode("jcr:content");
                } else {
                    contentNode = current.addNode("jcr:content", "nt:resource");
                }
                contentNode.setProperty("jcr:mimeType", "application/pdf");
                contentNode.setProperty("jcr:data", content);
            }
            if ("ldr:refName".equals(propName)) {
                key = strValue;
            }
        }
        createReferences(key, current.getIdentifier());
    }

    private void registerReferences(final Node currentNode, final org.w3c.dom.Node contentDefinition)
            throws XPathExpressionException, ParseException, ValueFormatException, VersionException, LockException,
            ConstraintViolationException, RepositoryException {
        final org.w3c.dom.NodeList propDefs = (org.w3c.dom.NodeList) xp.evaluate("property", contentDefinition,
                XPathConstants.NODESET);
        for (int i = 0; i < propDefs.getLength(); i++) {
            final org.w3c.dom.Node propDef = propDefs.item(i);
            final String propName = getNameAttribute(propDef);
            final String strValue = getText(propDef);
            if (propName.equals("ldr:ref")) {
                if (referenceMap.containsKey(strValue)) {
                    referenceMap.get(strValue).add(currentNode);
                } else {
                    final List<Node> nodeList = new ArrayList<>();
                    nodeList.add(currentNode);
                    referenceMap.put(strValue, nodeList);
                }
                LOG.info("Registered reference on {} to {}", currentNode.getName(), strValue);
            }
        }
    }

    private void createReferences(final String key, final String id)
            throws ValueFormatException, VersionException, LockException, ConstraintViolationException,
            RepositoryException {
        final List<Node> nodeList = referenceMap.get(key);
        LOG.info("Start adding {} reference to {} (= {})", nodeList.size(), key, id);
        for (int i = 0; i < nodeList.size(); i++) {
            final Node current = nodeList.get(i);
            final Value values[] = current.getProperty(IRM_CONTENT_LIST_PROPERTY).getValues();
            final String newValues[] = new String[values.length + 1];
            for (int v = 0; v < newValues.length - 1; v++) {
                newValues[v] = values[v].getString();
            }
            newValues[newValues.length - 1] = id;
            current.getProperty(IRM_CONTENT_LIST_PROPERTY).setValue(newValues);
            LOG.info("Add reference on {} to {} (= {})", current.getName(), key, id);
        }
    }

    private String getText(final org.w3c.dom.Node node)
            throws XPathExpressionException {
        return xp.evaluate("text()", node);
    }

    private String getTypeAttribute(final org.w3c.dom.Node node)
            throws XPathExpressionException {
        return getAttribute(node, "type");
    }

    private String getNameAttribute(final org.w3c.dom.Node node)
            throws XPathExpressionException {
        return getAttribute(node, "name");
    }

    private String getCreateAttribute(final org.w3c.dom.Node node)
            throws XPathExpressionException {
        return getAttribute(node, "create");
    }

    private String getAttribute(final org.w3c.dom.Node node, final String attribute)
            throws XPathExpressionException {
        return xp.evaluate('@' + attribute, node);
    }

    private InputStream getInputStreamFromURI(final String uri) {
        return JCRContentLoader.class.getResourceAsStream(uri);
    }

    public static void loadContent(final Session session, final URL demoContent) {
        try {
            final Repository repository = session.getRepository();

            final String user = session.getUserID();
            final String repositoryName = repository.getDescriptor(Repository.REP_NAME_DESC);
            LOG.info("Logged in as {} to a {} repository.", user, repositoryName);

            final InputStream is = demoContent.openStream();

            final JCRContentLoader init = new JCRContentLoader();
            final List<Node> nodes = init.initializeContent(session, is);

            for (final Node node : nodes) {
                LOG.info("{}", node.getPath());
                if (node.hasProperties()) {
                    final PropertyIterator pit = node.getProperties();
                    while (pit.hasNext()) {
                        final Property p = pit.nextProperty();
                        if (p.isMultiple()) {
                            LOG.info("  {} = (multiple values)", p.getName());
                        } else {
                            LOG.info("  {} = {}", p.getName(), p.getValue().getString());
                        }
                    }
                }
            }

        } catch (final Exception e) {
            LOG.error("", e);
        }
    }
}
