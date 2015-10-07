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
import static org.mockito.Mockito.when;
import io.undertow.servlet.api.DeploymentInfo;

import java.util.Collections;

import io.inkstand.config.ApplicationConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultResteasyDeploymentProviderTest {

    @Mock
    private ApplicationConfiguration config;

    @InjectMocks
    private DefaultResteasyDeploymentProvider subject;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        when(config.getContextRoot()).thenReturn("test");
        when(config.getProviderClasses()).thenReturn(Collections.EMPTY_LIST);
        when(config.getResourceClasses()).thenReturn(Collections.EMPTY_LIST);
    }

    @Test
    public void testGetDeployment() throws Exception {
        DeploymentInfo di = this.subject.getDeployment();
        assertNotNull(di);
        assertEquals("test", di.getContextPath());
        assertEquals("ResteasyUndertow", di.getDeploymentName());
    }

}
