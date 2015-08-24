package io.inkstand.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import org.junit.Before;
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
    private AnnotatedType annotatedType;

    @Mock
    private ProcessAnnotatedType pat;

    @Before
    public void setUp() throws Exception {
        when(pat.getAnnotatedType()).thenReturn(annotatedType);
    }

    @Test
    public void testServletFound() throws Exception {

        //prepare
        when(annotatedType.getJavaClass()).thenReturn(TestServlet.class);

        //act
        subject.servletFound(pat);

        //assert
        Set<Class> servlets = subject.getServlets();
        assertEquals(1, servlets.size());
        assertTrue(servlets.contains(TestServlet.class));

    }

    @Test
    public void testGetServlets_multipleHits_allReturned() throws Exception {
        //prepare
        when(annotatedType.getJavaClass()).thenReturn(QualifiedTestServlet.class);
        subject.servletFound(pat);
        when(annotatedType.getJavaClass()).thenReturn(TestServlet.class);
        subject.servletFound(pat);

        //act
        Set<Class> servlet = subject.getAllServlets();

        //assert
        assertNotNull(servlet);
        assertEquals(2, servlet.size());
        assertTrue(servlet.contains(QualifiedTestServlet.class));
        assertTrue(servlet.contains(TestServlet.class));

    }

    @Test
    public void testGetServlets_default_empty() throws Exception {

        //prepare

        //act
        Set<Class> classes = subject.getServlets();

        //assert
        assertNotNull(classes);
        assertTrue(classes.isEmpty());

    }

    @Test
    public void testGetServlets_withQualifiers_subsetReturned() throws Exception {
        //prepare
        when(annotatedType.getJavaClass()).thenReturn(TestServlet.class);
        subject.servletFound(pat);
        when(annotatedType.getJavaClass()).thenReturn(QualifiedTestServlet.class);
        subject.servletFound(pat);

        //act
        Set<Class> qualified = subject.getServlets(TestQualifier.class);

        //assert
        assertNotNull(qualified);
        assertEquals(1, qualified.size());
        assertTrue(qualified.contains(QualifiedTestServlet.class));

    }

    @Test
    public void testGetServlets_withMultipleQualifiers_subsetReturned() throws Exception {
        //prepare
        when(annotatedType.getJavaClass()).thenReturn(TestServlet.class);
        subject.servletFound(pat);
        when(annotatedType.getJavaClass()).thenReturn(QualifiedTestServlet.class);
        subject.servletFound(pat);
        when(annotatedType.getJavaClass()).thenReturn(MultiQualifiedTestServlet.class);
        subject.servletFound(pat);

        //act
        Set<Class> qualified = subject.getServlets(TestQualifier.class, AdditionalTestQualifier.class);

        //assert
        assertNotNull(qualified);
        assertEquals(1, qualified.size());
        assertTrue(qualified.contains(MultiQualifiedTestServlet.class));

    }

    @Test
    public void testGetServlets_withoutQualifiers_subsetReturned() throws Exception {
        //prepare
        when(annotatedType.getJavaClass()).thenReturn(TestServlet.class);
        subject.servletFound(pat);
        when(annotatedType.getJavaClass()).thenReturn(QualifiedTestServlet.class);
        subject.servletFound(pat);
        when(annotatedType.getJavaClass()).thenReturn(MultiQualifiedTestServlet.class);
        subject.servletFound(pat);

        //act
        Set<Class> qualified = subject.getServlets();

        //assert
        assertNotNull(qualified);
        assertEquals(1, qualified.size());
        assertTrue(qualified.contains(TestServlet.class));

    }

    @Test
    public void testGetAllServlets() throws Exception {
        //prepare
        when(annotatedType.getJavaClass()).thenReturn(TestServlet.class);
        subject.servletFound(pat);
        when(annotatedType.getJavaClass()).thenReturn(QualifiedTestServlet.class);
        subject.servletFound(pat);
        when(annotatedType.getJavaClass()).thenReturn(MultiQualifiedTestServlet.class);
        subject.servletFound(pat);

        //act
        Set<Class> servlets = subject.getAllServlets();

        //assert
        assertNotNull(servlets);
        assertEquals(3, servlets.size());
        assertTrue(servlets.contains(TestServlet.class));
        assertTrue(servlets.contains(QualifiedTestServlet.class));
        assertTrue(servlets.contains(MultiQualifiedTestServlet.class));

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    private @interface TestQualifier {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    private static @interface AdditionalTestQualifier {

    }


    private static class TestServlet {

    }

    @TestQualifier
     private static class QualifiedTestServlet {

    }

    @TestQualifier
    @AdditionalTestQualifier
    private static class MultiQualifiedTestServlet {

    }
}
