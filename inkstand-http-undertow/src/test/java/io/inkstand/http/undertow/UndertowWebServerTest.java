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
import static org.mockito.Mockito.verify;
import io.undertow.Undertow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UndertowWebServerTest {

    @Mock
    private Undertow undertow;

    @InjectMocks
    private UndertowWebServer subject;

    @Test
    public void testStart() throws Exception {
        subject.start();
        verify(undertow).start();
    }

    @Test
    public void testStop() throws Exception {
        subject.stop();
        verify(undertow).stop();
    }

    @Test
    public void testGetUndertow() throws Exception {
        assertEquals(undertow, subject.getUndertow());
    }

}
