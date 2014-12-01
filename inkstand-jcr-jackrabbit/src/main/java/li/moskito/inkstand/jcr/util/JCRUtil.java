package li.moskito.inkstand.jcr.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JCRUtil {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JCRUtil.class);

    private JCRUtil() {

    }

    /**
     * Creates a transient repository at the specified path using the specified configuration file
     * 
     * @param repositoryLocation
     * @param configUrl
     * @return
     * @throws ConfigurationException
     * @throws IOException
     */
    public static TransientRepository createTransientRepository(final File repositoryLocation, final URL configUrl)
            throws ConfigurationException, IOException {
        LOG.info("Creating transient repository at location {}", repositoryLocation.getAbsolutePath());

        final RepositoryConfig config = RepositoryConfig.create(configUrl.openStream(),
                repositoryLocation.getAbsolutePath());
        return new TransientRepository(config);
    }

    /**
     * Loads the inque nodetype model to the session's repository
     * 
     * @param session
     * @throws IOException
     * @throws RepositoryException
     * @throws ParseException
     */
    public static void initializeContentModel(final Session session)
            throws IOException, RepositoryException {
        final URL cndFile = JCRUtil.class.getResource("/inbox_jcr_model.cnd.txt");
        LOG.info("Initializing JCR Model from File {}", cndFile.getPath());

        final Reader cndReader = new InputStreamReader(cndFile.openStream());
        final Repository repository = session.getRepository();

        final String user = session.getUserID();
        final String name = repository.getDescriptor(Repository.REP_NAME_DESC);
        LOG.info("Logged in as {} to a {} repository", user, name);

        NodeType[] nodeTypes;
        try {
            nodeTypes = CndImporter.registerNodeTypes(cndReader, session, true);
            if (LOG.isDebugEnabled()) {
                logRegisteredNodeTypes(nodeTypes);
            }
        } catch (ParseException e) {
            throw new RuntimeException("Could not register node types", e);
        }
        
    }

    private static void logRegisteredNodeTypes(final NodeType[] nodeTypes) {
        final StringBuilder buf = new StringBuilder(32);
        for (final NodeType nt : nodeTypes) {
            buf.append(nt.getName()).append("\n\t  > ");
            String sep = "";
            for (final NodeType supert : nt.getSupertypes()) {
                buf.append(sep).append(supert.getName());
                sep = ", ";
            }
            buf.append("\n\t");
            for (final PropertyDefinition pd : nt.getDeclaredPropertyDefinitions()) {
                buf.append("  - ").append(pd.getName()).append(' ');
                switch (pd.getRequiredType()) {
                case PropertyType.BINARY:
                    buf.append("(BINARY)");
                    break;
                case PropertyType.BOOLEAN:
                    buf.append("(BOOLEAN)");
                    break;
                case PropertyType.DATE:
                    buf.append("(DATE)");
                    break;
                case PropertyType.DECIMAL:
                    buf.append("(DECIMAL)");
                    break;
                case PropertyType.DOUBLE:
                    buf.append("(DOUBLE)");
                    break;
                case PropertyType.LONG:
                    buf.append("(LONG)");
                    break;
                case PropertyType.NAME:
                    buf.append("(NAME)");
                    break;
                case PropertyType.PATH:
                    buf.append("(PATH)");
                    break;
                case PropertyType.REFERENCE:
                    buf.append("(REFERENCE)");
                    break;
                case PropertyType.WEAKREFERENCE:
                    buf.append("(WEAKREFERENCE)");
                    break;
                case PropertyType.STRING:
                    buf.append("(STRING)");
                    break;
                case PropertyType.UNDEFINED:
                    buf.append("(UNDEFINED)");
                    break;
                case PropertyType.URI:
                    buf.append("(URI)");
                    break;
                default:
                    buf.append("!unknown!");
                    break;
                }
                buf.append("\n\t");
            }
        }

        LOG.debug("Registered Node Types: [\n\t{}]", buf.toString());
    }

    public static void loadContent(final Session session, final URL contentDescription)
            throws ParserConfigurationException {
        JCRContentLoader.loadContent(session, contentDescription);
    }

}
