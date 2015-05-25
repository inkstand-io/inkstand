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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.inkstand.scribble.Scribble;

public class UndertowDefaultConfigurationTest {

    private UndertowDefaultConfiguration subject;

    @Before
    public void setUp() throws Exception {
        subject = new UndertowDefaultConfiguration();
        //SCRIB-7 injection of primitive types does not work
        Scribble.inject(1024).asConfigProperty("inkstand.http.port").into(subject);
        Scribble.inject("foreign.host").asConfigProperty("inkstand.http.listenaddress").into(subject);
    }

    //TODO reenable test
    @Test
    @Ignore
    public void testGetPort() throws Exception {
        assertEquals(1024, subject.getPort());
    }

    @Test
    public void testGetBindAddress() throws Exception {
        assertEquals("foreign.host", subject.getBindAddress());
    }

}
