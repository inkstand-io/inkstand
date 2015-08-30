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

package io.inkstand.http.undertow;

import static io.inkstand.scribble.Scribble.inject;
import static io.inkstand.scribble.net.NetworkMatchers.isAvailable;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import io.inkstand.scribble.net.NetworkUtils;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

@RunWith(MockitoJUnitRunner.class)
public class UndertowWebServerTest {

    private static final Logger LOG = getLogger(UndertowWebServerTest.class);

    private Undertow undertow;

    private UndertowWebServer subject;

    private int port;
    private URL serviceUrl;

    @Before
    public void setUp() throws Exception {
        port = NetworkUtils.findAvailablePort();
        serviceUrl = new URL("http://localhost:"+port);

        subject = new UndertowWebServer();
        undertow = Undertow.builder().addHttpListener(port, "localhost", new HttpHandler() {

            @Override
            public void handleRequest(final HttpServerExchange httpServerExchange) throws Exception {

                LOG.info("{} {}", httpServerExchange.getRequestMethod(), httpServerExchange.getRequestPath());
            }
        }).build();
        inject(undertow).into(subject);
    }

    @Test
    public void testStartStop() throws Exception {
        subject.start();
        assertThat(serviceUrl, isAvailable());

        subject.stop();
        Thread.sleep(1000);
        assertThat(serviceUrl, not(isAvailable()));
    }


    @Test
    public void testGetUndertow() throws Exception {
        assertEquals(undertow, subject.getUndertow());
    }

    @Test
    public void testToString() throws Exception {
        //prepare

        //act
        assertEquals("[Undertow]", subject.toString());
        //assert

    }

}
