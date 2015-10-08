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

import static io.inkstand.http.undertow.UndertowDefaultConfiguration.HTTP_HOSTNAME_PROPERTY;
import static io.inkstand.http.undertow.UndertowDefaultConfiguration.HTTP_PORT_PROPERTY;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.util.Properties;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Gerald on 01.08.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class UndertowLauncherArgsTest {

    /**
     * The class under test
     */
    @InjectMocks
    private UndertowLauncherArgs subject;
    private Properties originalProperties;

    @Before
    public void setUp() throws Exception {

        originalProperties = (Properties) System.getProperties().clone();

    }

    @After
    public void tearDown() throws Exception {

        System.setProperties(originalProperties);
    }

    @Test
    public void testGetArgNames() throws Exception {
        //prepare

        //act
        Set<String> argNames = subject.getArgNames();

        //assert
        assertNotNull(argNames);
        assertEquals(2, argNames.size());
        assertTrue(argNames.contains("port"));
        assertTrue(argNames.contains("hostname"));

    }

    @Test
    public void testApply_hostname() throws Exception {

        //prepare
        assumeThat(System.getProperty(HTTP_HOSTNAME_PROPERTY), nullValue());
        assumeThat(System.getProperty(HTTP_PORT_PROPERTY), nullValue());

        //act
        subject.apply("hostname", "testhostname");

        //assert
        assertEquals("testhostname", System.getProperty(HTTP_HOSTNAME_PROPERTY));
        assertNull(System.getProperty(HTTP_PORT_PROPERTY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApply_hostname_noValue() throws Exception {

        //act
        subject.apply("hostname", null);

    }

    @Test
    public void testApply_port() throws Exception {

        //prepare
        assumeThat(System.getProperty(HTTP_PORT_PROPERTY), nullValue());
        assumeThat(System.getProperty(HTTP_HOSTNAME_PROPERTY), nullValue());

        //act
        subject.apply("port", "1080");

        //assert
        assertEquals("1080", System.getProperty(HTTP_PORT_PROPERTY));
        assertNull(System.getProperty(HTTP_HOSTNAME_PROPERTY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApply_port_noValue() throws Exception {

        //act
        subject.apply("port", null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testApply_unknownArg() throws Exception {

        //prepare

        //act
        subject.apply("unknown", "1080");

    }

    @Test
    public void testGetDescription_hostname() throws Exception {

        //prepare

        //act
        String desc = subject.getDescription("hostname");

        //assert
        assertEquals("The hostname of the http server", desc);
    }

    @Test
    public void testGetDescription_port() throws Exception {

        //prepare

        //act
        String desc = subject.getDescription("port");

        //assert
        assertEquals("The TCP port the http server accepts incoming requests.", desc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDescription_unknownArg() throws Exception {

        //prepare

        //act
        subject.getDescription("unknown");

    }
}
