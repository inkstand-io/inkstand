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

package io.inkstand.it;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.slf4j.LoggerFactory.getLogger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import io.inkstand.Inkstand;
import io.inkstand.scribble.net.NetworkUtils;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * Created by Gerald on 09.08.2015.
 */
public class JMXManagementITCase {

    private static final Logger LOG = getLogger(JMXManagementITCase.class);

    private int port;
    private int mgmtPort;
    private Properties originalProperties;

    /**
     * method to start the instance for demo purposes.
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        JMXManagementITCase it = new JMXManagementITCase();
        it.setUp();
        System.setProperty("inkstand.http.port", String.valueOf(it.port));
        System.setProperty("inkstand.mgmt.port", String.valueOf(it.mgmtPort));
        Inkstand.main();
    }

    @Before
    public void setUp() throws Exception {
        port = NetworkUtils.findAvailablePort();
        mgmtPort = NetworkUtils.findAvailablePort();
        originalProperties = System.getProperties();
        LOG.info("Preparing Inkstand, public port: {}, management port: {}", port, mgmtPort);
    }

    @After
    public void tearDown() throws Exception {
        CdiContainerLoader.getCdiContainer().shutdown();
        System.setProperties(originalProperties);
    }

    @Test
    public void testManagementService_jmx_root() throws Exception {
        //prepare
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));
        System.setProperty("inkstand.mgmt.port", String.valueOf(mgmtPort));
        final Client client = ClientBuilder.newClient();

        //act
        Inkstand.main(new String[] {});

        //assert
        verifyPublicServiceRunning(client);
        final WebTarget statusSvc = getManagementService(client).path("jmx/");
        final JsonObject links = introspectService(statusSvc);
        verifyLinks(statusSvc, links);

    }

    private void verifyLinks(final WebTarget statusSvc, final JsonObject links) {

        for(Map.Entry<String, JsonValue> entry : links.entrySet()){
            final JsonObject linkObject = (JsonObject)entry.getValue();
            final WebTarget linkTarget = statusSvc.path(linkObject.getString("href"));
            LOG.info("REQ {}" ,linkTarget.getUri());
            final Response linkResponse = linkTarget.request(APPLICATION_JSON_TYPE).get();
            assertEquals(200, linkResponse.getStatus());
            JsonObject json = readJson(linkResponse);
            assertFalse(json.entrySet().isEmpty());
        }
    }

    private JsonObject introspectService(final WebTarget statusSvc) {

        LOG.info("REQ {}" ,statusSvc.getUri());
        final Response response = statusSvc.request(APPLICATION_JSON_TYPE).get();
        return readJson(response).getJsonObject("_links");
    }

    /**
     * Creates a web target for the management service
     * @param client
     * @return
     */
    private WebTarget getManagementService(final Client client) {

        return client.target("http://localhost:" + mgmtPort).path("inkstand/rest/");
    }


    /**
     * Verifies, the public service is running on the public port
     * @param client
     */
    private void verifyPublicServiceRunning(final Client client) {

        final WebTarget publicService = client.target("http://localhost:" + port).path("test");
        //public service is running
        assertEquals("test", publicService.request().get(String.class));
    }

    private static JsonObject readJson(final Response response) {

        String strRespones = response.readEntity(String.class);
        LOG.info("RCV {}" , strRespones);
        final StringReader stringReader = new StringReader(strRespones);
        final JsonObject json;
        try (JsonReader jsonReader = Json.createReader(stringReader)) {
            json = jsonReader.readObject();
        }
        return json;
    }
}
