/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.jcr.provider;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.xml.parsers.ParserConfigurationException;

import io.inkstand.InkstandRuntimeException;
import io.inkstand.jcr.util.JCRContentLoader;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JackrabbitUtil {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JackrabbitUtil.class);

    private JackrabbitUtil() {

    }

    /**
     * Creates a transient repository at the specified path using the specified configuration file
     *
     * @param repositoryLocation
     *            the home directory of the repository
     * @param configUrl
     *            the URL to the configuration file
     * @return an instance of a transient repository
     * @throws ConfigurationException
     *   when the configuration file is was invalid
     */
    public static TransientRepository createTransientRepository(final File repositoryLocation, final URL configUrl)
            throws ConfigurationException {
        LOG.info("Creating transient repository at location {}", repositoryLocation.getAbsolutePath());

        RepositoryConfig config;
        try {
            config = RepositoryConfig.create(configUrl.openStream(), repositoryLocation.getAbsolutePath());
        } catch (final IOException e) {
            throw new InkstandRuntimeException("Could not read config url " + configUrl, e);
        }
        return new TransientRepository(config);
    }

    /**
     * Loads the inque nodetype model to the session's repository
     *
     * @param session
     *  a session with a user with sufficient privileges
     * @param cndFile
     *  the url to the file containing the node type definitions in CND syntax
     * @throws RepositoryException
     */
    public static void initializeContentModel(final Session session, final URL cndFile) throws RepositoryException {
        LOG.info("Initializing JCR Model from File {}", cndFile.getPath());

        Reader cndReader;
        try {
            cndReader = new InputStreamReader(cndFile.openStream(), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new InkstandRuntimeException("Could not read cndFile " + cndFile, e);
        }
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
        } catch (final ParseException | IOException e) {
            throw new InkstandRuntimeException("Could not register node types", e);
        }

    }

    private static void logRegisteredNodeTypes(final NodeType[] nodeTypes) {
        final StringBuilder buf = new StringBuilder(32);
        for (final NodeType nt : nodeTypes) {
            buf.append(nt.getName()).append("\n\t  > ");
            String sep = "";
            for (final NodeType supertype : nt.getSupertypes()) {
                buf.append(sep).append(supertype.getName());
                sep = ", ";
            }
            buf.append("\n\t");
            for (final PropertyDefinition pd : nt.getDeclaredPropertyDefinitions()) {
                buf.append("  - ").append(pd.getName()).append(" (");
                buf.append(PropertyType.nameFromValue(pd.getRequiredType())).append(')');
                buf.append("\n\t");
            }
        }

        LOG.debug("Registered Node Types: [\n\t{}]", buf.toString());
    }

    /**
     * Loads content into the repository.
     *
     * @param session
     *            the session to load the content into the repository
     * @param contentDescription
     *            the URL of the content description file
     * @throws ParserConfigurationException
     */
    public static void loadContent(final Session session, final URL contentDescription)
            throws ParserConfigurationException {
        new JCRContentLoader().loadContent(session, contentDescription);
    }

}
