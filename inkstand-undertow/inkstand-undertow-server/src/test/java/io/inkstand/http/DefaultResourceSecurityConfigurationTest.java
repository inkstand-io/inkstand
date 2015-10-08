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

package io.inkstand.http;

import static io.inkstand.scribble.Scribble.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Gerald on 17.06.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultResourceSecurityConfigurationTest {

    /**
     * The class under test
     */
    @InjectMocks
    private DefaultResourceSecurityConfiguration subject;

    @Test
    public void testGetRealm() throws Exception {
        //prepare
        inject("testRealm").asConfigProperty("inkstand.http.auth.realm").into(subject);

        //act
        String result = subject.getRealm();

        //assert
        assertEquals("testRealm", result);
    }

    @Test
    public void testGetRealm_default() throws Exception {
        //prepare
        inject(null).asConfigProperty("inkstand.http.auth.realm").into(subject);

        //act
        String result = subject.getRealm();

        //assert
        assertEquals("Inkstand", result);
    }

    @Test
    public void testGetAuthenticationMethod() throws Exception {
        //prepare
        inject("DIGEST").asConfigProperty("inkstand.http.auth.method").into(subject);

        //act
        String result = subject.getAuthenticationMethod();

        //assert
        assertEquals("DIGEST", result);
    }

    @Test
    public void testGetAuthenticationMethod_default() throws Exception {
        //prepare
        inject(null).asConfigProperty("inkstand.http.auth.method").into(subject);

        //act
        String result = subject.getAuthenticationMethod();

        //assert
        assertEquals("BASIC", result);
    }

    @Test
    public void testGetSecurityRoles_default() throws Exception {
        //prepare

        //act
        Set<String> result = subject.getSecurityRoles();

        //assert
        assertNotNull(result);
        assertTrue(result.contains("Users"));
    } @Test
    public void testGetSecurityRoles() throws Exception {
        //prepare
        subject.setSecurityRoles("users,admins");

        //act
        Set<String> result = subject.getSecurityRoles();

        //assert
        assertNotNull(result);
        assertTrue(result.contains("users"));
        assertTrue(result.contains("admins"));
    }

    @Test
    public void testGetAllowedRoles_default() throws Exception {
        //prepare

        //act
        Set<String> result = subject.getAllowedRoles();

        //assert
        assertNotNull(result);
        assertTrue(result.contains("Users"));
    } @Test
    public void testGetAllowedRoles() throws Exception {
        //prepare
        subject.setAllowedRoles("users,admins");

        //act
        Set<String> result = subject.getAllowedRoles();

        //assert
        assertNotNull(result);
        assertTrue(result.contains("users"));
        assertTrue(result.contains("admins"));
    }

    @Test
    public void testGetProtectedResources_default() throws Exception {
        //prepare

        //act
        Set<String> result = subject.getProtectedResources();

        //assert
        assertNotNull(result);
        assertTrue(result.contains("/*"));
    }@Test
    public void testGetProtectedResources() throws Exception {
        //prepare
        subject.setProtectedResourcs("/auth/*;/api/secure/*");

        //act
        Set<String> result = subject.getProtectedResources();

        //assert
        assertNotNull(result);
        assertTrue(result.contains("/auth/*"));
        assertTrue(result.contains("/api/secure/*"));
    }
}
