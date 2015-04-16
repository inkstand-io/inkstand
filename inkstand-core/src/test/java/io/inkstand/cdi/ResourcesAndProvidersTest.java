package io.inkstand.cdi;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ResourcesAndProvidersTest {
    
    @SuppressWarnings("rawtypes")
    @Mock
    private ProcessAnnotatedType pat;
    @SuppressWarnings("rawtypes")
    @Mock
    private AnnotatedType type;

    private ResourcesAndProviders subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.subject = new ResourcesAndProviders();
        when(pat.getAnnotatedType()).thenReturn(type);
    }

    @Test
    public void testPathFound() throws Exception {
        //prepare
        when(type.getJavaClass()).thenReturn(ResourcesAndProvidersTest.class);
        //act
        this.subject.pathFound(pat);
        //assert
        assertTrue(this.subject.getResourceClasses().contains(ResourcesAndProvidersTest.class));
        assertTrue(this.subject.getProviderClasses().isEmpty());
    }

    @Test
    public void testProviderFound() throws Exception {
        //prepare
        when(type.getJavaClass()).thenReturn(ResourcesAndProvidersTest.class);
        //act
        this.subject.providerFound(pat);
        //assert
        assertTrue(this.subject.getProviderClasses().contains(ResourcesAndProvidersTest.class));
        assertTrue(this.subject.getResourceClasses().isEmpty());
    }

}
