/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.deployment.resteasy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import io.inkstand.Management;
import io.inkstand.cdi.ResourcesAndProviders;
import io.inkstand.scribble.Scribble;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
        //prepare
        when(scanner.getProviderClasses()).thenReturn(Arrays.<Class>asList(Resource.class, ManagementResource.class));
        when(scanner.getProviderClasses(Management.class)).thenReturn(Arrays.<Class>asList(ManagementResource.class));
        //act
        Collection<Class> providers = subject.getProviderClasses();

        //assert
        assertNotNull(providers);
        assertEquals(1, providers.size());
        assertTrue(providers.contains(Resource.class));
    }

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Test
    public void testGetResourceClasses() throws Exception {
        //prepare
        when(scanner.getResourceClasses()).thenReturn(Arrays.<Class>asList(Resource.class, ManagementResource.class));
        when(scanner.getResourceClasses(Management.class)).thenReturn(Arrays.<Class>asList(ManagementResource.class));
        //act
        Collection<Class> resource = subject.getResourceClasses();

        //assert
        assertNotNull(resource);
        assertEquals(1, resource.size());
        assertTrue(resource.contains(Resource.class));
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

    static class Resource {}

    @Management
    static class ManagementResource{

    }

}
