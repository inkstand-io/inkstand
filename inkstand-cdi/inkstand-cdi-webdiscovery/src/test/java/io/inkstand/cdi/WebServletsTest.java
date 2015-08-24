package io.inkstand.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Gerald Mücke on 24.08.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class WebServletsTest {

    /**
     * The class under test
     */
    @InjectMocks
    private WebServlets subject;

    @Mock
    private ProcessAnnotatedType pat;

    @Test
    public void testServletFound() throws Exception {

        //prepare
        when(pat.getAnnotatedType().getJavaClass()).thenReturn(TestServlet.class);

        //act
        subject.servletFound(pat);

        //assert
        Set<Class> servlets = subject.getServlets();
        assertEquals(1, servlets.size());
        assertTrue(servlets.contains(TestServlet.class));

    }

@Test
    public void testGetServlets_default_empty() throws Exception {

        //prepare

        //act
        Set<Class> classes =  subject.getServlets();

        //assert
        assertNotNull(classes);
        assertTrue(classes.isEmpty());

    }

    private static class TestServlet {}
}
