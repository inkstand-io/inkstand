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
