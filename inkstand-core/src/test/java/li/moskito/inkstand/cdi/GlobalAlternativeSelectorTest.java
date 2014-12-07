package li.moskito.inkstand.cdi;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GlobalAlternativeSelectorTest {

    @SuppressWarnings("rawtypes")
    @Mock
    private ProcessAnnotatedType pat;
    @SuppressWarnings("rawtypes")
    @Mock
    private AnnotatedType at;
    @Mock
    private BeforeBeanDiscovery bbd;
    @Mock
    private AfterTypeDiscovery atd;

    private GlobalAlternativeSelector subject;
    private ClassLoader ccl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.subject = new GlobalAlternativeSelector();
        this.subject.loadApplicationAlternatives(bbd);
        this.ccl = Thread.currentThread().getContextClassLoader();
        when(pat.getAnnotatedType()).thenReturn(at);

    }

    @After
    public void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(ccl);
    }

    @Test(expected = IllegalStateException.class)
    public void testLoadApplicationAlternatives_noCCL_noBeansXml() throws Exception {
        Thread.currentThread().setContextClassLoader(null);
        this.subject.loadApplicationAlternatives(bbd);
    }

    @Test(expected = IllegalStateException.class)
    public void testLoadApplicationAlternatives_CCL_noBeansXml() throws Exception {
        ClassLoader cl = mock(ClassLoader.class);
        Thread.currentThread().setContextClassLoader(cl);
        this.subject.loadApplicationAlternatives(bbd);
    }

    @Test
    public void testWatchAlternatives_noGlobalAlternative() throws Exception {
        // prepare, no global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(false);
        // act
        this.subject.watchAlternatives(pat);
        // assert, not vetoed
        verify(pat, times(0)).veto();

    }

    @Test
    public void testWatchAlternatives_enabledAlternativeClass() throws Exception {
        // prepare, global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(true);
        when(at.getJavaClass()).thenReturn(AlternativeClass.class);
        // act
        this.subject.watchAlternatives(pat);
        // assert, not vetoed
        verify(pat, times(0)).veto();
    }

    @Test
    public void testWatchAlternatives_disabledAlternativeClass() throws Exception {
        // prepare, global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(true);
        when(at.getJavaClass()).thenReturn(DisabledAlternativeClass.class);
        // act
        this.subject.watchAlternatives(pat);
        // assert, vetoed
        verify(pat).veto();
    }

    @Test
    public void testWatchAlternatives_stereotypedClass_enable() throws Exception {
        // prepare, global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(true);
        when(at.isAnnotationPresent(TestStereotype.class)).thenReturn(true);
        when(at.getJavaClass()).thenReturn(StereotypedClass.class);
        // act
        this.subject.watchAlternatives(pat);
        // assert, not vetoed
        verify(pat, times(0)).veto();
    }

    @Test
    public void testWatchAlternatives_unstereotypedClass_veto() throws Exception {
        // prepare, global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(true);
        when(at.isAnnotationPresent(TestStereotype.class)).thenReturn(false);
        when(at.getJavaClass()).thenReturn(UnstereotypedClass.class);
        // act
        this.subject.watchAlternatives(pat);
        // assert, vetoed
        verify(pat).veto();
    }

    @Test
    public void testWatchAlternatives_stereotypedMethod_enable() throws Exception {
        // prepare, global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(true);
        when(at.isAnnotationPresent(TestStereotype.class)).thenReturn(true);
        when(at.getJavaClass()).thenReturn(StereotypedMethod.class);
        // act
        this.subject.watchAlternatives(pat);
        // assert, not vetoed
        verify(pat, times(0)).veto();
    }

    @Test
    public void testWatchAlternatives_unstereotypedMethod_veto() throws Exception {
        // prepare, global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(true);
        when(at.isAnnotationPresent(TestStereotype.class)).thenReturn(false);
        when(at.getJavaClass()).thenReturn(UnstereotypedMethod.class);
        // act
        this.subject.watchAlternatives(pat);
        // assert, vetoed
        verify(pat).veto();
    }

    @Test
    public void testWatchAlternatives_stereotypedField_enable() throws Exception {
        // prepare, global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(true);
        when(at.isAnnotationPresent(TestStereotype.class)).thenReturn(true);
        when(at.getJavaClass()).thenReturn(StereotypedField.class);
        // act
        this.subject.watchAlternatives(pat);
        // assert, not vetoed
        verify(pat, times(0)).veto();
    }

    @Test
    public void testWatchAlternatives_unstereotypedField_veto() throws Exception {
        // prepare, global alternative
        when(at.isAnnotationPresent(Priority.class)).thenReturn(true);
        when(at.isAnnotationPresent(TestStereotype.class)).thenReturn(false);
        when(at.getJavaClass()).thenReturn(UnstereotypedField.class);
        // act
        this.subject.watchAlternatives(pat);
        // assert, vetoed
        verify(pat).veto();
    }

    @Test
    public void testAfterTypeDiscovery() throws Exception {
        // no assertion, the method should just not throw an exception as it performs logging only
        this.subject.afterTypeDiscovery(atd);

    }

    @Alternative
    public static class AlternativeClass {

    }

    @Alternative
    public static class DisabledAlternativeClass {

    }

    @Stereotype
    @ApplicationScoped
    @Alternative
    @Retention(RetentionPolicy.RUNTIME)
    @Target({
            ElementType.TYPE, ElementType.METHOD, ElementType.FIELD
    })
    public static @interface TestStereotype {

    }

    @TestStereotype
    @Priority(1)
    public static class StereotypedClass {

    }

    @Priority(1)
    public static class StereotypedMethod {

        @Produces
        @TestStereotype
        public Object produce() {
            return new Object();
        }

    }

    @Priority(1)
    public static class StereotypedField {
        @TestStereotype
        @Produces
        private Object producer = new Object();
    }

    public static class UnstereotypedClass {
        @Produces
        private Object object;

    }

    public static class UnstereotypedMethod {
        @Produces
        public Object produces() {
            return new Object();
        }
    }

    public static class UnstereotypedField {
        @Produces
        private Object produces;
    }

}