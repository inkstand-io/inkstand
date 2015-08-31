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

package io.inkstand.it;

import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;

import io.inkstand.Inkstand;
import io.inkstand.scribble.net.NetworkUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * Created by Gerald on 09.08.2015.
 */
public class ManagementITCase {

    private static final Logger LOG = getLogger(ManagementITCase.class);

    private int port;
    private int mgmtPort;
    private Properties originalProperties;

    @Before
    public void setUp() throws Exception {
        port = NetworkUtils.findAvailablePort();
        mgmtPort = NetworkUtils.findAvailablePort();
        originalProperties = System.getProperties();
    }

    @After
    public void tearDown() throws Exception {
        System.setProperties(originalProperties);
    }
    @Test
    public void testManagementService_queryStatus() throws Exception {
        //prepare
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));
        System.setProperty("inkstand.mgmt.port", String.valueOf(mgmtPort));
        final Client client = ClientBuilder.newClient();

        //act
        Inkstand.main(new String[] {});
        verifyPublicServiceRunning(client);
        final WebTarget statusSvc = getManagementService(client).path("control/status/");
        final Response response = statusSvc.request(MediaType.APPLICATION_JSON_TYPE).get();

        //assert
        JsonObject json = readJson(response);
        assertEquals("RUNNING", json.getString("state"));
    }

    @Test
    public void testManagementService_jmxStatus() throws Exception {
        //prepare
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));
        System.setProperty("inkstand.mgmt.port", String.valueOf(mgmtPort));
        final Client client = ClientBuilder.newClient();

        //act
        Inkstand.main(new String[] {});
        verifyPublicServiceRunning(client);
        final WebTarget statusSvc = getManagementService(client).path("jmx/");
        final Response response = statusSvc.request(MediaType.APPLICATION_JSON_TYPE).get();

        //assert
        JsonObject json = readJson(response);

    }

    /**
     * Creates a web target for the management service
     * @param client
     * @return
     */
    private WebTarget getManagementService(final Client client) {

        return client.target("http://localhost:" + mgmtPort).path("inkstand");
    }

    @Test
    public void testManagementService() throws Exception {
        //prepare
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));
        System.setProperty("inkstand.mgmt.port", String.valueOf(mgmtPort));

        //act
        Inkstand.main(new String[] {});

        //assert
        final Client client = ClientBuilder.newClient();
        final WebTarget publicService = client.target("http://localhost:" + port).path("test");
        final WebTarget mgmtService = getManagementService(client);

        //public service is running
        String publicServiceResult = publicService.request().get(String.class);
        assertEquals("test", publicServiceResult);

        //Post shutdown comand the mgmtService
        StringWriter writer = new StringWriter();
        final JsonGenerator out = Json.createGenerator(writer);
        out.writeStartObject();
        out.write("delay", "5");
        out.writeEnd();
        out.close();
        final Response response = mgmtService.request(MediaType.APPLICATION_JSON)
                                             .post(Entity.entity(writer.toString(), MediaType.APPLICATION_JSON_TYPE));

        //Se Response.Status.OK;
        assertEquals(200, response.getStatus());
        final JsonObject json = readJson(response);
        assertEquals("ok", json.getString("msg"));

        LOG.info("Waiting for service to shutdown");
        Thread.sleep(10000);
        LOG.info("Trying service");
        final Response validateResponse = mgmtService.request(MediaType.APPLICATION_JSON).get();
        JsonObject json2 = readJson(validateResponse);
        assertEquals("ok", json2.getString("msg"));
        LOG.info("Done");
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
        LOG.info("RCV: {}" , strRespones);
        final StringReader stringReader = new StringReader(strRespones);
        final JsonObject json;
        try (JsonReader jsonReader = Json.createReader(stringReader)) {
            json = jsonReader.readObject();
        }
        return json;
    }
}
