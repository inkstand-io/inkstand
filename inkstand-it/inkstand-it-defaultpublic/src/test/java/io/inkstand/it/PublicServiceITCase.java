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

import javax.ws.rs.client.ClientBuilder;
import java.util.Properties;

import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.inkstand.Inkstand;
import io.inkstand.scribble.net.NetworkUtils;

/**
 * Created by Gerald on 27.05.2015.
 */
public class PublicServiceITCase {

    private int port;
    private Properties originalProperties;

    @Before
    public void setUp() throws Exception {
        port = NetworkUtils.findAvailablePort();
        originalProperties = System.getProperties();
    }

    @After
    public void tearDown() throws Exception {
        System.setProperties(originalProperties);
        CdiContainerLoader.getCdiContainer().shutdown();
    }

    @Test
    public void testGetApp_syspropsConfiguration() throws InterruptedException {

        //prepare
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));

        //act
        Inkstand.main(new String[]{});

        //assert
        String value = ClientBuilder.newClient()
                                    .target("http://localhost:" + port + "/test")
                                    .request().get(String.class);
        Assert.assertEquals("test", value);
    }

    @Test
    public void testGetApp_cmdLineConfiguration() throws InterruptedException {

        //prepare
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        String[] args = new String[]{"-port", port + ""};

        //act
        Inkstand.main(args);

        //assert
        String value = ClientBuilder.newClient()
                                    .target("http://localhost:" + port + "/test")
                                    .request().get(String.class);
        Assert.assertEquals("test", value);
    }
}
