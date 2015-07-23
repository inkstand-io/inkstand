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

package io.inkstand.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by Gerald on 23.07.2015.
 */
public class SearchScopeTest {

    @Test
    public void testGetValue() throws Exception {
        //act
        assertEquals(0, LdapAuthConfiguration.SearchScope.BASE.getValue());
        assertEquals(1, LdapAuthConfiguration.SearchScope.ONE_LEVEL.getValue());
        assertEquals(2, LdapAuthConfiguration.SearchScope.SUBTREE.getValue());

    }

    @Test
    public void testGetName() throws Exception {
        //act
        assertEquals("base", LdapAuthConfiguration.SearchScope.BASE.getName());
        assertEquals("one", LdapAuthConfiguration.SearchScope.ONE_LEVEL.getName());
        assertEquals("sub", LdapAuthConfiguration.SearchScope.SUBTREE.getName());

    }
}
