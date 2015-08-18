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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.Properties;

import io.inkstand.Inkstand;
import io.inkstand.scribble.net.NetworkUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Gerald on 09.08.2015.
 */
public class ManagementITCase {

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
        final WebTarget mgmtService = client.target("http://localhost:" + mgmtPort).path("mgmt");

        //public service is running
        String publicServiceResult = publicService.request().get(String.class);
        assertEquals("test", publicServiceResult);

        //Query the mgmtService
        final Response response = mgmtService.request(MediaType.APPLICATION_JSON).get();

        //Se Response.Status.OK;
        assertEquals(200, response.getStatus());
        final JsonObject json = readJson(response);
        assertEquals("ok", json.getString("msg"));
    }

    private JsonObject readJson(final Response response) {

        final StringReader stringReader = new StringReader(response.readEntity(String.class));
        final JsonObject json;
        try (JsonReader jsonReader = Json.createReader(stringReader)) {
            json = jsonReader.readObject();
        }
        return json;
    }
}
