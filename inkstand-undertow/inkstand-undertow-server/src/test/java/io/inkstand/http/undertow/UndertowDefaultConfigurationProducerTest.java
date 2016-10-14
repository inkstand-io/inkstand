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

package io.inkstand.http.undertow;

import static io.inkstand.scribble.Scribble.inject;
import static org.junit.Assert.assertEquals;

import io.inkstand.config.WebServerConfiguration;
import org.junit.Before;
import org.junit.Test;

public class UndertowDefaultConfigurationProducerTest {

    private UndertowDefaultConfigurationProducer subject;

    @Before
    public void setUp() throws Exception {
        subject = new UndertowDefaultConfigurationProducer();

    }

    @Test
    public void testGetConfiguration() throws Exception {

        //prepare
        inject(1024).asConfigProperty("inkstand.http.port").into(subject);
        inject("foreign.host").asConfigProperty("inkstand.http.listenaddress").into(subject);

        //act
        WebServerConfiguration config = subject.getConfiguration();

        //assert
        assertEquals(1024, config.getPort());
        assertEquals("foreign.host", config.getBindAddress());
    }

    @Test
    public void testGetBindAddress_defaultValues() throws Exception {
        //prepare
        inject(null).asConfigProperty("inkstand.http.port").into(subject);
        inject(null).asConfigProperty("inkstand.http.listenaddress").into(subject);

        //act
        WebServerConfiguration config = subject.getConfiguration();

        //assert
        assertEquals(80, config.getPort());
        assertEquals("localhost", config.getBindAddress());
    }

}
