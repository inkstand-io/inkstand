package io.inkstand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class InkstandExceptionTest {

    private RuntimeException cause;

    @Before
    public void setUp() throws Exception {
        cause = new RuntimeException();
    }

    @Test
    public void testInkstandException() throws Exception {
        final InkstandException subject = new InkstandException();
        assertNull(subject.getMessage());
        assertNull(subject.getCause());
    }

    @Test
    public void testInkstandExceptionStringThrowable() throws Exception {
        final InkstandException subject = new InkstandException("message", cause);
        assertEquals("message", subject.getMessage());
        assertEquals(cause, subject.getCause());
    }

    @Test
    public void testInkstandExceptionString() throws Exception {
        final InkstandException subject = new InkstandException("message");
        assertEquals("message", subject.getMessage());
        assertNull(subject.getCause());
    }

    @Test
    public void testInkstandExceptionThrowable() throws Exception {
        final InkstandException subject = new InkstandException(cause);
        assertEquals(cause.toString(), subject.getMessage());
        assertEquals(cause, subject.getCause());
    }
}
