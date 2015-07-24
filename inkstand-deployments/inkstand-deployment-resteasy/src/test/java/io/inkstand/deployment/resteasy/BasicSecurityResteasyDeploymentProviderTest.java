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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.config.ApplicationConfiguration;
import io.inkstand.config.ResourceSecurityConfiguration;
import io.undertow.servlet.api.AuthMethodConfig;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.LoginConfig;
import io.undertow.servlet.api.SecurityConstraint;
import io.undertow.servlet.api.WebResourceCollection;

/**
 * Created by Gerald on 17.06.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class BasicSecurityResteasyDeploymentProviderTest {

    @Mock
    private ApplicationConfiguration appConfig;

    @Mock
    private ResourceSecurityConfiguration secConfig;

    /**
     * The class under test
     */
    @InjectMocks
    private BasicSecurityResteasyDeploymentProvider subject;

    @Test
    public void testGetDeployment() throws Exception {

        //prepare
        when(secConfig.getRealm()).thenReturn("TestRealm");
        when(secConfig.getAuthenticationMethod()).thenReturn("Basic");
        when(secConfig.getSecurityRoles()).thenReturn(asSet("admin", "users"));
        when(secConfig.getProtectedResources()).thenReturn(asSet("/*"));
        when(secConfig.getAllowedRoles()).thenReturn(asSet("admin"));

        //act
        DeploymentInfo di = subject.getDeployment();

        //assert
        assertNotNull(di);

        //verify security roles of deployment
        final Set<String> secRoles = di.getSecurityRoles();
        assertTrue(secRoles.contains("admin"));
        assertTrue(secRoles.contains("users"));

        //verify login configuration for deployment
        final LoginConfig lc = di.getLoginConfig();
        assertNotNull(lc);
        assertEquals("TestRealm", lc.getRealmName());
        final List<AuthMethodConfig> authMethods =  lc.getAuthMethods();
        assertEquals(1, authMethods.size());
        assertEquals("Basic", authMethods.get(0).getName());

        //verify the security constraints
        final List<SecurityConstraint> secConstraints = di.getSecurityConstraints();
        assertEquals(1, secConstraints.size());
        final SecurityConstraint sc = secConstraints.get(0);
        final Set<String> rolesAllowed = sc.getRolesAllowed();
        assertEquals(1, rolesAllowed.size());
        assertTrue(rolesAllowed.contains("admin"));
        final Set<WebResourceCollection> protectedResources = sc.getWebResourceCollections();
        assertEquals(1, protectedResources.size());
        final WebResourceCollection wrc = protectedResources.iterator().next();
        assertEquals(1, wrc.getUrlPatterns().size());
        assertTrue(wrc.getUrlPatterns().contains("/*"));


    }

    private Set<String> asSet(String... params) {
        final Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(params));
        return set;
    }

}
