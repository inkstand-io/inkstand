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

package io.inkstand.http.undertow.auth.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.Principal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class LdapAccountTest {

    private static final String TEST_USER_ID = "testUserId";
    private static final String TEST_USER_DN = "uid=testUserId, ou=users, ou=system";
    private LdapAccount subject;

    @Before
    public void setUp() throws Exception {
        subject = new LdapAccount(TEST_USER_ID, TEST_USER_DN);
    }

    @Test
    public void testGetPrincipal() throws Exception {
        final Principal p = subject.getPrincipal();
        assertNotNull(p);
        assertEquals(TEST_USER_ID, p.getName());
    }

    @Test
    public void testGetRoles() throws Exception {
        assertNotNull(subject.getRoles());
        assertTrue(subject.getRoles().isEmpty());
    }

    @Test
    public void testAddRole() throws Exception {
        subject.addRole("testRole");
        assertTrue(subject.getRoles().contains("testRole"));
    }

    @Test
    public void testAddRoles() throws Exception {
        subject.addRoles(Arrays.asList("testRole1", "testRole2"));
        assertTrue(subject.getRoles().contains("testRole1"));
        assertTrue(subject.getRoles().contains("testRole2"));
    }

    @Test
    public void testGetDn() throws Exception {
        assertEquals(TEST_USER_DN, subject.getUserDn());
    }

}
