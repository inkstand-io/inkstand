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

package io.inkstand.http.undertow;

import static io.inkstand.scribble.Scribble.inject;
import static io.inkstand.scribble.net.NetworkMatchers.isAvailable;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.config.ResourceSecurityConfiguration;
import io.inkstand.config.WebServerConfiguration;
import io.inkstand.scribble.net.NetworkUtils;
import io.undertow.Undertow;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentInfo;

/**
 * Created by Gerald on 29.05.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticatingUndertowWebServerProviderTest {

    @Mock
    private WebServerConfiguration config;

    @Mock
    private ResourceSecurityConfiguration securityConfig;

    @Mock
    private IdentityManager identityManager;

    @Mock
    private Account account;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DeploymentInfo deploymentInfo;

    /**
     * The class under test
     */
    @InjectMocks
    private AuthenticatingUndertowWebServerProvider subject;

    private int port;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {

        this.port = NetworkUtils.findAvailablePort();
        when(config.getBindAddress()).thenReturn("localhost");
        when(config.getPort()).thenReturn(port);
        when(securityConfig.getRealm()).thenReturn("testRealm");
        when(securityConfig.getAuthenticationMethod()).thenReturn("BASIC");
        when(deploymentInfo.clone()).thenReturn(deploymentInfo);
        when(deploymentInfo.getDefaultEncoding()).thenReturn("UTF-8");
        when(deploymentInfo.getDeploymentName()).thenReturn("test.war");
        when(deploymentInfo.getContextPath()).thenReturn("test");
        when(deploymentInfo.getThreadSetupActions()).thenReturn(Collections.EMPTY_LIST);
    }

    @Test
     public void testGetSecuredUndertow_protectedResource_noAuth_fail() throws Exception {
        //prepare
        inject(config).asQualifyingInstance().into(subject);
        inject(securityConfig).asQualifyingInstance().into(subject);

        //act
        Undertow undertow = subject.getSecuredUndertow();

        //assert
        assertNotNull(undertow);
        verify(deploymentInfo).setIdentityManager(identityManager);
        try {
            //the test will fail as the server protects the resource
            undertow.start();
            assertThat(new URL("http://localhost:" + port + "/test"), not(isAvailable()));
        } finally {
            undertow.stop();
        }
    }


    @Test
    public void testAddSecurity() throws Exception {

        //prepare
        inject(config).asQualifyingInstance().into(subject);
        inject(securityConfig).asQualifyingInstance().into(subject);
        HttpHandler noAuthHandler = mock(HttpHandler.class);

        //act
        HttpHandler authHandler = subject.addSecurity(noAuthHandler);

        //assert
        assertNotNull(authHandler);
        assertNotEquals(authHandler, noAuthHandler);
    }
}
