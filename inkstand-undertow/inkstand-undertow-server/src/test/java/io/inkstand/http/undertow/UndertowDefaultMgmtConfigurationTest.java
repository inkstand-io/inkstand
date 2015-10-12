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
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Gerald on 12.10.2015.
 */
public class UndertowDefaultMgmtConfigurationTest {

    private UndertowDefaultMgmtConfiguration subject;

    @Before
    public void setUp() throws Exception {
        subject = new UndertowDefaultMgmtConfiguration();
        inject(1024).asConfigProperty("inkstand.mgmt.port").into(subject);
        inject("foreign.host").asConfigProperty("inkstand.mgmt.listenaddress").into(subject);
    }

    @Test
    public void testGetPort() throws Exception {
        assertEquals(1024, subject.getPort());
    }
   @Test
    public void testGetPort_default() throws Exception {
       //prepare
       inject(null).asConfigProperty("inkstand.mgmt.port").into(subject);

       //act
       //assert
       assertEquals(7999, subject.getPort());

    }

    @Test
    public void testGetBindAddress() throws Exception {
        assertEquals("foreign.host", subject.getBindAddress());
    }
    @Test
    public void testGetBindAddress_default() throws Exception {
        inject(null).asConfigProperty("inkstand.mgmt.listenaddress").into(subject);
        assertEquals("localhost", subject.getBindAddress());
    }
}
