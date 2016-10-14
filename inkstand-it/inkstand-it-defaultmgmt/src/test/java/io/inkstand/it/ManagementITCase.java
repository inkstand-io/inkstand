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

import static io.inkstand.scribble.net.NetworkMatchers.isAvailable;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.MediaType.APPLICATION_ATOM_XML;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import io.inkstand.Inkstand;
import io.inkstand.scribble.net.NetworkUtils;

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
        LOG.info("Preparing Inkstand, public port: {}, management port: {}", port, mgmtPort);
    }

    @After
    public void tearDown() throws Exception {
        CdiContainerLoader.getCdiContainer().shutdown();
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
        LOG.info("REQ GET {}", statusSvc.getUri());
        final Response response = statusSvc.request(APPLICATION_JSON_TYPE).get();

        //assert
        JsonObject json = readJson(response);
        assertEquals("RUNNING", json.getString("state"));
    }

    @Test
    public void testManagementService_queryStatus_unsupportedMediaType_406() throws Exception {
        //prepare
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));
        System.setProperty("inkstand.mgmt.port", String.valueOf(mgmtPort));
        final Client client = ClientBuilder.newClient();

        //act
        Inkstand.main(new String[] {});
        verifyPublicServiceRunning(client);
        final WebTarget statusSvc = getManagementService(client).path("control/status/");
        LOG.info("REQ GET {}", statusSvc.getUri());
        final Response response = statusSvc.request(APPLICATION_ATOM_XML).get();

        //assert
        assertEquals(406, response.getStatus());
        JsonObject json = readJson(response);
        assertEquals("ContentType: application/atom+xml not supported", json.getString("message"));
    }

    @Test
    public void testManagementService_shutdown() throws Exception {
        //prepare
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));
        System.setProperty("inkstand.mgmt.port", String.valueOf(mgmtPort));
        final Client client = ClientBuilder.newClient();

        //act
        Inkstand.main(new String[] {});
        verifyPublicServiceRunning(client);

        //assert
        final WebTarget statusSvc = getManagementService(client).path("control/shutdown/");

        //Post shutdown comand the mgmtService
        LOG.info("REQ POST {}", statusSvc.getUri());
        final Response response = statusSvc.request(APPLICATION_JSON_TYPE).post(createShutdownEntity(5));

        //Se Response.Status.OK;
        assertEquals(200, response.getStatus());
        final JsonObject json = readJson(response);
        assertEquals("Shutdown request received", json.getString("msg"));

        LOG.info("Waiting for service to shutdown");
        Thread.sleep(10000);
        LOG.info("Trying service");
        assertThat(new URL("http://localhost:" + port + "/test"), not(isAvailable()));
        LOG.info("Done");
    }

    private Entity createShutdownEntity(final int delay) {

        final StringWriter writer = new StringWriter();
        try(final JsonGenerator out = Json.createGenerator(writer)) {
            out.writeStartObject();
            out.write("delay", delay);
            out.writeEnd();
        }
        return entity(writer.toString(), APPLICATION_JSON_TYPE);
    }

    /**
     * Creates a web target for the management service
     * @param client
     * @return
     */
    private WebTarget getManagementService(final Client client) {

        return client.target("http://localhost:" + mgmtPort).path("inkstand/servlet");
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
