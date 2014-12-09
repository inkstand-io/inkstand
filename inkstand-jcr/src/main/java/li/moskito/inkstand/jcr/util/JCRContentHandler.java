package li.moskito.inkstand.jcr.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import li.moskito.schemas.jcr_import.ObjectFactory;
import li.moskito.schemas.jcr_import.PropertyDescriptor;
import li.moskito.schemas.jcr_import.PropertyValueType;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Implementation of the {@link DefaultHandler} that creates {@link Node} in a JCR {@link Repository} that are defined
 * in an xml file.
 * 
 * @author Gerald Muecke, gerald@moskito.li
 */
@SuppressWarnings("unchecked")
public class JCRContentHandler extends DefaultHandler {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JCRContentHandler.class);
    /**
     * Namespace the content handler uses to identify the correct elements
     */
    public static final String INKSTAND_IMPORT_NAMESPACE = "http://www.moskito.li/schemas/jcr-import";

    /**
     * Object Factory for creating temporary model object.
     */
    private static final ObjectFactory FACTORY = new ObjectFactory();

    /**
     * The session used for import operations
     */
    private Session session;

    /**
     * The start time in ns.
     */
    private long startTime;

    /**
     * A stack of the created nodes.
     */
    private Deque<Node> nodeStack;
    /**
     * A stack of the created text elements
     */
    private Deque<String> textStack;
    // TODO verify if text stack could be replaced by lastText
    /**
     * A stack fo the created property descriptors.
     */
    private Deque<PropertyDescriptor> propertyStack;

    /**
     * Map of {@link PropertyValueType} to the int values of the {@link PropertyType}
     */
    private static final Map<PropertyValueType, Integer> JCR_PROPERTIES;

    static {
        Map<PropertyValueType, Integer> properties = new HashMap<>();
        //@formatter:off
        properties.put(PropertyValueType.BINARY,        PropertyType.BINARY);
        properties.put(PropertyValueType.DATE,          PropertyType.DATE);
        properties.put(PropertyValueType.DECIMAL,       PropertyType.DECIMAL);
        properties.put(PropertyValueType.DOUBLE,        PropertyType.DOUBLE);
        properties.put(PropertyValueType.LONG,          PropertyType.LONG);
        properties.put(PropertyValueType.NAME,          PropertyType.NAME);
        properties.put(PropertyValueType.PATH,          PropertyType.PATH);
        properties.put(PropertyValueType.REFERENCE,     PropertyType.REFERENCE);
        properties.put(PropertyValueType.STRING,        PropertyType.STRING);
        properties.put(PropertyValueType.UNDEFINED,     PropertyType.UNDEFINED);
        properties.put(PropertyValueType.URI,           PropertyType.URI);
        properties.put(PropertyValueType.WEAKREFERENCE, PropertyType.WEAKREFERENCE);
        // @formatter:on
        JCR_PROPERTIES = Collections.unmodifiableMap(properties);
    }

    // TODO verify if propertyStack could be replaced by lastProperty

    /**
     * Creates a new content handler using the specified session for performing the input
     * 
     * @param session
     */
    public JCRContentHandler(Session session) {
        this.session = session;
        this.nodeStack = new ArrayDeque<>();
        this.textStack = new ArrayDeque<>();
        this.propertyStack = new ArrayDeque<>();
    }

    /**
     * Prints out information statements and sets the startTimer
     */
    @Override
    public void startDocument() throws SAXException {
        LOG.info("BEGIN ContentImport");
        LOG.info("IMPORT USER: {}", session.getUserID());
        this.startTime = System.nanoTime();
    }

    /**
     * Persists the changes in the repository and prints out information such as processing time
     */
    @Override
    public void endDocument() throws SAXException {
        LOG.info("Content Processing finished, saving...");
        try {
            session.save();
        } catch (RepositoryException e) {
            throw new SAXException("Saving failed", e);
        }
        long endTime = System.nanoTime();
        long processingTime = endTime - startTime;
        LOG.info("Content imported in {} ms", processingTime / 1_000_000);
        LOG.info("END ContentImport");
    }

    /**
     * Depending on the element, which has to be in the correct namespace, the method either creates a new node, adds a
     * mixin type or creates a property (properties are not yet written to the node)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        LOG.trace("startElement uri={} localName={} qName={} attributes={}", uri, localName, qName, attributes);

        if (!isInkstandNamespace(uri)) {
            return;
        }
        switch (localName) {
            case "rootNode":
                startElementRootNode(attributes);
                break;
            case "node":
                startElementNode(attributes);
                break;
            case "mixin":
                startElementMixin(attributes);
                break;
            case "property":
                startElementProperty(attributes);
                break;
            default:
                break;
        }
    }

    /**
     * Invoked on rootNode element
     * 
     * @param attributes
     * @throws SAXException
     */
    private void startElementRootNode(Attributes attributes) throws SAXException {
        LOG.debug("Found rootNode");
        try {
            this.nodeStack.push(newNode(null, attributes));
        } catch (RepositoryException e) {
            throw new SAXException("Could not create node", e);
        }
    }

    /**
     * Invoked on node element
     * 
     * @param attributes
     * @throws SAXException
     */
    private void startElementNode(Attributes attributes) throws SAXException {
        LOG.debug("Found node");
        try {
            this.nodeStack.push(newNode(this.nodeStack.peek(), attributes));
        } catch (RepositoryException e) {
            throw new SAXException("Could not create node", e);
        }
    }

    /**
     * Invoked on mixin element
     * 
     * @param attributes
     * @throws SAXException
     */
    private void startElementMixin(Attributes attributes) throws SAXException {
        LOG.debug("Found mixin declaration");
        try {
            addMixin(this.nodeStack.peek(), attributes);
        } catch (RepositoryException e) {
            throw new SAXException("Could not add mixin type", e);
        }
    }

    /**
     * Invoked on property element
     * 
     * @param attributes
     */
    private void startElementProperty(Attributes attributes) {
        LOG.debug("Found property");
        propertyStack.push(newPropertyDescriptor(attributes));
    }

    /**
     * Depending on the element, which has to be in the correct namespace, the method adds a property to the node or
     * removes completed nodes from the node stack.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        LOG.trace("endElement uri={} localName={} qName={}", uri, localName, qName);
        if (isInkstandNamespace(uri)) {
            switch (localName) {
                case "rootNode":
                    LOG.debug("Closing rootNode");
                    this.nodeStack.pop();
                    break;
                case "node":
                    LOG.debug("Closing node");
                    this.nodeStack.pop();
                    break;
                case "mixin":
                    LOG.debug("Closing mixin");
                    break;
                case "property":
                    endElementProperty();
                    break;
                default:
                    break;
            }
        }
    }

    private void endElementProperty() throws SAXException {
        LOG.debug("Closing property");
        PropertyDescriptor pd = propertyStack.pop();
        try {
            pd.setValue(parseValue(pd.getJcrType(), textStack.pop()));
            addProperty(this.nodeStack.peek(), pd);
        } catch (RepositoryException e) {
            throw new SAXException("Could set property value", e);
        }
    }

    /**
     * Creates the {@link Node} in the repository from the given attributes
     * 
     * @param parent
     *            the parent node of the node to be created. If this is null, a root-level node will be created.
     * @param attributes
     *            the attributes containing the basic information required to create the node
     * @return the newly creates {@link Node}
     * @throws RepositoryException
     */
    private Node newNode(Node parent, Attributes attributes) throws RepositoryException {
        Node parentNode;
        if (parent == null) {
            parentNode = session.getRootNode();
        } else {
            parentNode = parent;
        }
        // TODO handle path paramters

        String name = attributes.getValue("name");
        String primaryType = attributes.getValue("primaryType");

        LOG.info("Node {} adding child node {}(type={})", parentNode.getPath(), name, primaryType);
        Node node = parentNode.addNode(name, primaryType);
        return node;
    }

    private void addMixin(Node node, Attributes attributes) throws RepositoryException {
        String mixinType = attributes.getValue("name");
        LOG.info("Node {} adding mixin {}", node.getPath(), mixinType);
        node.addMixin(mixinType);
    }

    /**
     * Adds a property to the node. The property's name, type and value is defined in the {@link PropertyDescriptor}
     * 
     * @param node
     *            the node to which the property should be added
     * @param pd
     *            the {@link PropertyDescriptor} containing the details of the property
     * @throws RepositoryException
     */
    private void addProperty(Node node, PropertyDescriptor pd) throws RepositoryException {
        LOG.info("Node {} adding property {}", node.getPath(), pd.getName());
        node.setProperty(pd.getName(), (Value) pd.getValue());
    }

    /**
     * Creates a new {@link PropertyDescriptor} from the attributes
     * 
     * @param attributes
     *            the attributes defining the name and jcrType of the property
     * @return a {@link PropertyDescriptor} instance
     */
    private PropertyDescriptor newPropertyDescriptor(Attributes attributes) {
        PropertyDescriptor pd = FACTORY.createPropertyDescriptor();
        LOG.debug("property name={}", attributes.getValue("name"));
        LOG.debug("property jcrType={}", attributes.getValue("jcrType"));
        pd.setName(attributes.getValue("name"));
        pd.setJcrType(PropertyValueType.fromValue(attributes.getValue("jcrType")));
        return pd;
    }

    private Object parseValue(PropertyValueType valueType, String valueAsText) throws RepositoryException {
        // TODO handle ref property
        LOG.debug("Parsing type={} from='{}'", valueType, valueAsText);
        final ValueFactory vf = session.getValueFactory();
        Value value = null;
        switch (valueType) {
            case BINARY:
                value = vf.createValue(vf.createBinary(new ByteArrayInputStream(Base64.decodeBase64(valueAsText
                        .getBytes()))));
                break;
            case REFERENCE:
                // TODO resolve IDs
                value = null;
                break;
            case WEAKREFERENCE:
                // TODO resolve IDs
                value = null;
                break;
            default:
                value = vf.createValue(valueAsText, getPropertyType(valueType));
        }

        return value;
    }

    /**
     * Converts the valueType to an int representing the {@link PropertyType} of the property.
     * 
     * @param valueType
     *            the value type to be converted
     * @return the int value of the corresponding {@link PropertyType}
     */
    private int getPropertyType(PropertyValueType valueType) {
        return JCR_PROPERTIES.get(valueType).intValue();
    }

    /**
     * Checks if the specified uri is of the namesspace this {@link JCRContentHandler} is able to process
     * 
     * @param uri
     *            the uri to check
     * @return <code>true</code> if the namespace is processable by this {@link JCRContentHandler}
     */
    private boolean isInkstandNamespace(String uri) {
        return INKSTAND_IMPORT_NAMESPACE.equals(uri);
    }

    /**
     * Detects text by trimming the effective content of the char array.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String text = new String(ch).substring(start, start + length);
        LOG.trace("characters; '{}'", text);
        String trimmedText = text.trim();
        if (!trimmedText.isEmpty()) {
            LOG.info("text: '{}'", trimmedText);
            this.textStack.push(trimmedText);
        }
    }
}
