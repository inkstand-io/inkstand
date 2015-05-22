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
import static io.inkstand.scribble.net.NetworkMatchers.isReachable;
import static io.inkstand.scribble.net.NetworkMatchers.remotePort;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import io.inkstand.scribble.net.NetworkUtils;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UndertowWebServerTest {


    private Undertow undertow;

    private UndertowWebServer subject;

    private int port;

    @Before
    public void setUp() throws Exception {

        subject = new UndertowWebServer();
        port = NetworkUtils.findAvailablePort();
        undertow = Undertow.builder().addHttpListener(port, "localhost", new HttpHandler() {

            @Override
            public void handleRequest(final HttpServerExchange httpServerExchange) throws Exception {

                System.out.println(httpServerExchange.getRequestMethod() + " " + httpServerExchange.getRequestPath());
            }
        }).build();
        inject(undertow).into(subject);
    }

    @Test
    public void testStartStop() throws Exception {

        System.out.println("starting server on port " + port);
        subject.start();
        assertThat(remotePort("localhost", port), isReachable().within(10, TimeUnit.SECONDS));
        subject.stop();
        assertThat(remotePort("localhost", port), not(isReachable().within(10, TimeUnit.SECONDS)));
    }


    @Test
    public void testGetUndertow() throws Exception {
        assertEquals(undertow, subject.getUndertow());
    }

}
