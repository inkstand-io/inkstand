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

package io.inkstand.deployment.resteasy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.scribble.Scribble;
import io.inkstand.cdi.ResourcesAndProviders;

@RunWith(MockitoJUnitRunner.class)
public class DynamicResteasyConfigurationTest {

    @Mock
    private ResourcesAndProviders scanner;

    @InjectMocks
    private DynamicResteasyConfiguration subject;

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Test
    public void testGetProviderClasses() throws Exception {
        final Collection<Class> providers = mock(Collection.class);
        when(scanner.getProviderClasses()).thenReturn(providers);
        assertEquals(providers, subject.getProviderClasses());
    }

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Test
    public void testGetResourceClasses() throws Exception {
        final Collection<Class> resources = mock(Collection.class);
        when(scanner.getResourceClasses()).thenReturn(resources);
        assertEquals(resources, subject.getResourceClasses());
    }

    @Test
    public void testGetContextRoot_configured() throws Exception {
        Scribble.inject("root").asConfigProperty("inkstand.rest.contextRoot").into(subject);
        assertEquals("root", subject.getContextRoot());
    }

    @Test
    public void testGetContextRoot_unconfigured() throws Exception {
        assertEquals("", subject.getContextRoot());
    }

}
