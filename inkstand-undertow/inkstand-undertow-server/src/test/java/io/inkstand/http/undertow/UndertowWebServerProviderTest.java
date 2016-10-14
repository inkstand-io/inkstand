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

package io.inkstand.http.undertow;

import static io.inkstand.scribble.Scribble.inject;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import io.inkstand.Management;
import io.inkstand.config.WebServerConfiguration;
import io.inkstand.scribble.net.NetworkUtils;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UndertowWebServerProviderTest {

    @Mock
    private WebServerConfiguration config;

    @Mock
    private Instance<DeploymentInfo> deploymentInfo;
    @Mock
    private Instance<WebServerConfiguration> mgmtConfig;
    @Mock
    private Instance<DeploymentInfo> mgmtDeployments;

    @InjectMocks
    private UndertowWebServerProvider subject;
    private int port;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        this.port = NetworkUtils.findAvailablePort();
        when(config.getBindAddress()).thenReturn("localhost");
        when(config.getPort()).thenReturn(port);

        final DeploymentInfo di = createDeploymentInfo("test");
        when(deploymentInfo.get()).thenReturn(di);
        when(deploymentInfo.iterator()).thenReturn(asList(di).iterator());

        //default: there are no management extensions
        when(mgmtConfig.isUnsatisfied()).thenReturn(true);
        when(mgmtDeployments.isUnsatisfied()).thenReturn(true);

        inject(mgmtConfig).asQualifyingInstance(Management.class).into(subject);
        inject(mgmtDeployments).asQualifyingInstance(Management.class).into(subject);
        inject(deploymentInfo).asQualifyingInstance(Default.class).into(subject);
    }

    private DeploymentInfo createDeploymentInfo(String contextRoot) {

        DeploymentInfo deploymentInfo = mock(DeploymentInfo.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        when(deploymentInfo.clone()).thenReturn(deploymentInfo);
        when(deploymentInfo.getDefaultEncoding()).thenReturn("UTF-8");
        when(deploymentInfo.getDeploymentName()).thenReturn("test.war");
        when(deploymentInfo.getContextPath()).thenReturn(contextRoot);
        when(deploymentInfo.getThreadSetupActions()).thenReturn(Collections.EMPTY_LIST);
        return deploymentInfo;
    }

    @Test
    public void testGetUndertow_singleDeployment() throws Exception {

        Undertow undertow = subject.getUndertow();
        assertNotNull(undertow);
        verifyDeployments(undertow, "http://localhost:"+this.port+"/test");

    }

    @Test
    public void testGetUndertow_multiDeployment() throws Exception {

        //prepare
        final DeploymentInfo di1 = createDeploymentInfo("test1");
        final DeploymentInfo di2 = createDeploymentInfo("test2");
        when(deploymentInfo.get()).thenReturn(di1);
        when(deploymentInfo.iterator()).thenReturn(asList(di1, di2).iterator());

        //act
        Undertow undertow = subject.getUndertow();

        //assert
        assertNotNull(undertow);

        //act
        verifyDeployments(undertow, "http://localhost:"+this.port+"/test1", "http://localhost:"+this.port+"/test2");
    }

    private void verifyDeployments(final Undertow undertow, final String... urls) throws IOException {

        try {
            undertow.start();
            for (String target : urls) {
                URL url = new URL(target);
                url.openConnection().connect();
            }
        } finally {
            undertow.stop();
        }
    }

}
