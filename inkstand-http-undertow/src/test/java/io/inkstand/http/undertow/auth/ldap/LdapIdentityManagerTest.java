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

package io.inkstand.http.undertow.auth.ldap;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.scribble.rules.ldap.Directory;
import io.inkstand.scribble.rules.ldap.DirectoryServer;
import io.inkstand.security.LdapAuthConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class LdapIdentityManagerTest {

    @Mock
    private LdapAuthConfiguration authConfig;

    @InjectMocks
    private LdapIdentityManager subject;

    public final TemporaryFolder folder = new TemporaryFolder();
    public final Directory directory = new Directory(folder);
    public final DirectoryServer ldapServer = new DirectoryServer(directory);
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(folder).around(directory).around(ldapServer);

    @Before
    public void setUp() throws Exception {
        when(authConfig.getHostname()).thenReturn("localhost");
    }


    @Test
    @Ignore
    public void testConnect() throws Exception {

        Thread.sleep(10000);
    }

    // @Test
    // public void testDisconnect() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }
    //
    // @Test
    // public void testVerifyAccount() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }
    //
    // @Test
    // public void testVerifyStringCredential() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }
    //
    // @Test
    // public void testVerifyCredential() throws Exception {
    // throw new RuntimeException("not yet implemented");
    // }

}
