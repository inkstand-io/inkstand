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

package io.inkstand.config;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Gerald Muecke on 13.10.2015.
 */
public class SimpleWebServerConfigurationTest {

    /**
     * The class under test
     */
    private SimpleWebServerConfiguration subject;

    @Before
    public void setUp() throws Exception {
        subject = new SimpleWebServerConfiguration("testhost", 12345);
    }

    @Test
    public void testGetPort() throws Exception {

        assertEquals(12345, subject.getPort());
    }

    @Test
    public void testGetBindAddress() throws Exception {
        assertEquals("testhost", subject.getBindAddress());
    }
}
