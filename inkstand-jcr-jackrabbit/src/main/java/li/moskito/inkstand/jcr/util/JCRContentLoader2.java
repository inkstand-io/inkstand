package li.moskito.inkstand.jcr.util;

import java.io.IOException;
import java.net.URL;

import javax.jcr.Session;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;

import li.moskito.inkstand.InkstandRuntimeException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Loads content from an external source into the JCR Repository
 * 
 * @author Gerald Muecke, gerald@moskito.li
 */
public class JCRContentLoader2 {

    private boolean validateInput = false;
    private boolean namespaceAware = true;
    private Schema schema = null;

    /**
     * Loads the content from the specified contentDefinition into the JCRRepository, using the specified session
     * 
     * @param session
     *            the session used to import the date. The user bound to the session must have the required privileges
     *            to perform the import operation.
     * @param contentDefinitionResource
     *            the content definition describing which content to import.
     */
    public void loadContent(Session session, URL contentDefinitionResource) {
        SAXParserFactory factory = getSAXParserFactory();
        try {
            SAXParser parser = factory.newSAXParser();
            InputSource source = new InputSource(contentDefinitionResource.openStream());
            parser.parse(source, new JCRContentHandler(session));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new InkstandRuntimeException("Loading Content to JCR Repository failed", e);
        }

    }

    /**
     * Creates a new {@link SAXParserFactory} using the configured parameters.
     * 
     * @return
     */
    private SAXParserFactory getSAXParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(namespaceAware);
        factory.setValidating(validateInput);
        if (this.schema != null) {
            factory.setSchema(schema);
        }
        return factory;
    }

    /**
     * Configures the loader to validate the input files
     * 
     * @param validateInput
     *            <code>true</code> if input should be validated. Default is <code>false</code>;
     */
    public void setValidateInput(boolean validateInput) {
        this.validateInput = validateInput;
    }

    /**
     * Configures the loader to be aware of namespaces
     * 
     * @param namespaceAware
     *            <code>true</code> if namespaces should be recognized. Default is <code>true</code>
     */
    public void setNamespaceAware(boolean namespaceAware) {
        this.namespaceAware = namespaceAware;
    }

    // TODO link to published schema
    /**
     * Sets a specific schema to validate the input against. Default is the inkstandLoader schema
     * 
     * @param schema
     *            schema to validate the input against
     */
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

}