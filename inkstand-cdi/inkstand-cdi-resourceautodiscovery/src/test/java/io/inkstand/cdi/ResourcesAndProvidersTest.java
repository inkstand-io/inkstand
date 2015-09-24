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
import java.util.Collection;

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
        assertTrue(this.subject.getProviderClasses(AnnotationA.class).isEmpty());
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

    @Test
    public void testGetResource_notAnnotated_noAnnotation_all() throws Exception {
        //prepare
        when(type.getJavaClass()).thenReturn(Resource.class);
        subject.pathFound(pat);
        when(type.getJavaClass()).thenReturn(AResource.class);
        subject.pathFound(pat);
        when(type.getJavaClass()).thenReturn(ABResource.class);
        subject.pathFound(pat);

        //act
        Collection<Class> resources = subject.getResourceClasses();

        //assert
        assertNotNull(resources);
        assertEquals(3, resources.size());
        assertTrue(resources.contains(Resource.class));
        assertTrue(resources.contains(AResource.class));
        assertTrue(resources.contains(ABResource.class));

    }


    @Test
    public void testGetResource_oneAnnotation_twoResult() throws Exception {
        //prepare
        when(type.getJavaClass()).thenReturn(Resource.class);
        subject.pathFound(pat);
        when(type.getJavaClass()).thenReturn(AResource.class);
        subject.pathFound(pat);
        when(type.getJavaClass()).thenReturn(ABResource.class);
        subject.pathFound(pat);

        //act
        Collection<Class> resources = subject.getResourceClasses(AnnotationA.class);

        //assert
        assertNotNull(resources);
        assertEquals(2, resources.size());
        assertTrue(resources.contains(AResource.class));
        assertTrue(resources.contains(ABResource.class));
    }

    @Test
    public void testGetResource_manyAnnotations() throws Exception {
        //prepare
        when(type.getJavaClass()).thenReturn(Resource.class);
        subject.pathFound(pat);
        when(type.getJavaClass()).thenReturn(AResource.class);
        subject.pathFound(pat);
        when(type.getJavaClass()).thenReturn(ABResource.class);
        subject.pathFound(pat);

        //act
        Collection<Class> resources = subject.getResourceClasses(AnnotationA.class, AnnotationB.class);

        //assert
        assertNotNull(resources);
        assertEquals(1, resources.size());
        assertTrue(resources.contains(ABResource.class));

    }

    @Test
    public void testGetProviders_notAnnotated_noAnnotation_all() throws Exception {
        //prepare
        when(type.getJavaClass()).thenReturn(Resource.class);
        subject.providerFound(pat);
        when(type.getJavaClass()).thenReturn(AResource.class);
        subject.providerFound(pat);
        when(type.getJavaClass()).thenReturn(ABResource.class);
        subject.providerFound(pat);

        //act
        Collection<Class> resources = subject.getProviderClasses();

        //assert
        assertNotNull(resources);
        assertEquals(3, resources.size());
        assertTrue(resources.contains(Resource.class));
        assertTrue(resources.contains(AResource.class));
        assertTrue(resources.contains(ABResource.class));

    }


    @Test
    public void testGetProviders_oneAnnotation_twoResult() throws Exception {
        //prepare
        when(type.getJavaClass()).thenReturn(Resource.class);
        subject.providerFound(pat);
        when(type.getJavaClass()).thenReturn(AResource.class);
        subject.providerFound(pat);
        when(type.getJavaClass()).thenReturn(ABResource.class);
        subject.providerFound(pat);

        //act
        Collection<Class> resources = subject.getProviderClasses(AnnotationA.class);

        //assert
        assertNotNull(resources);
        assertEquals(2, resources.size());
        assertTrue(resources.contains(AResource.class));
        assertTrue(resources.contains(ABResource.class));
    }

    @Test
    public void testGetProvider_manyAnnotations() throws Exception {
        //prepare
        when(type.getJavaClass()).thenReturn(Resource.class);
        subject.providerFound(pat);
        when(type.getJavaClass()).thenReturn(AResource.class);
        subject.providerFound(pat);
        when(type.getJavaClass()).thenReturn(ABResource.class);
        subject.providerFound(pat);

        //act
        Collection<Class> resources = subject.getProviderClasses(AnnotationA.class, AnnotationB.class);

        //assert
        assertNotNull(resources);
        assertEquals(1, resources.size());
        assertTrue(resources.contains(ABResource.class));

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @interface AnnotationA {

    }
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @interface AnnotationB {

    }

    static class Resource {}
    @AnnotationA
    static class AResource {}
    @AnnotationA
    @AnnotationB
    static class ABResource {}

}
