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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
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
