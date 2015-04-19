package io.inkstand.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SimplePrincipalTest {

    @Test
    public void testGetName() throws Exception {
        final SimplePrincipal subject = new SimplePrincipal("test");
        assertEquals("test", subject.getName());
    }

}
