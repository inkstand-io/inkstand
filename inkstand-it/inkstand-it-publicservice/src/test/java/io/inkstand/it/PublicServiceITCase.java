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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.inkstand.Inkstand;
import io.inkstand.scribble.net.NetworkUtils;

/**
 *
 * Created by Gerald on 27.05.2015.
 */
public class PublicServiceITCase {

    private int port;

    @Before
    public void setUp() throws Exception {
        port = NetworkUtils.findAvailablePort();
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));

    }

    @Test
    public void testGetApp() throws InterruptedException {

        //prepare
        Inkstand.main(new String[]{});

        //act
        String value = ClientBuilder.newClient()
                                    .target("http://localhost:" + port + "/test")
                                    .request().get(String.class);
        //assert
        Assert.assertEquals("test", value);
    }
}
