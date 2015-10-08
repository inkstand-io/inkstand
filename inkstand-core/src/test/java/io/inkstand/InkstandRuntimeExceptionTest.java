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

package io.inkstand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class InkstandRuntimeExceptionTest {

    private RuntimeException cause;

    @Before
    public void setUp() throws Exception {
        cause = new RuntimeException();
    }

    @Test
    public void testInkstandRuntimeException() throws Exception {
        final InkstandRuntimeException subject = new InkstandRuntimeException();
        assertNull(subject.getMessage());
        assertNull(subject.getCause());
    }

    @Test
    public void testInkstandRuntimeExceptionStringThrowable() throws Exception {
        final InkstandRuntimeException subject = new InkstandRuntimeException("message", cause);
        assertEquals("message", subject.getMessage());
        assertEquals(cause, subject.getCause());
    }

    @Test
    public void testInkstandRuntimeExceptionString() throws Exception {
        final InkstandRuntimeException subject = new InkstandRuntimeException("message");
        assertEquals("message", subject.getMessage());
        assertNull(subject.getCause());
    }

    @Test
    public void testInkstandRuntimeExceptionThrowable() throws Exception {
        final InkstandRuntimeException subject = new InkstandRuntimeException(cause);
        assertEquals(cause.toString(), subject.getMessage());
        assertEquals(cause, subject.getCause());
    }
}
