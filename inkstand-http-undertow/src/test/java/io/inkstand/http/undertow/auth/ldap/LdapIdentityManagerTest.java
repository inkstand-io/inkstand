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

import static io.inkstand.scribble.Scribble.newDirectory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.net.URL;

import io.inkstand.scribble.rules.ldap.DirectoryServer;
import io.inkstand.security.LdapAuthConfiguration;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.PasswordCredential;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LdapIdentityManagerTest {

    @Mock
    private LdapAuthConfiguration authConfig;

    @InjectMocks
    private LdapIdentityManager subject;

    private final URL ldif = LdapIdentityManagerTest.class.getResource("LdapIdentityManagerTest_users.ldif");

    @Rule
    public final DirectoryServer ldapServer = newDirectory().withPartition("inkstand", "dc=inkstand")
                                                  .importLdif(ldif)
                                                  .aroundDirectoryServer().onAvailablePort().build();
    private int port;

    @Before
    public void setUp() throws Exception {
        this.port = ldapServer.getTcpPort();
        when(authConfig.getPort()).thenReturn(port);
        when(authConfig.getHostname()).thenReturn("localhost");
    }


    @Test
    public void testVerify_withConnection_and_validUser() throws Exception {

        //prepare
        //bind settings
        when(authConfig.getBindDn()).thenReturn("uid=admin,ou=system");
        when(authConfig.getBindCredentials()).thenReturn("secret");
        //user search settings
        when(authConfig.getUserContextDn()).thenReturn("ou=users,dc=inkstand");
        when(authConfig.getUserFilter()).thenReturn("(uid={0})");
        //role settings
        when(authConfig.getRoleNameAttribute()).thenReturn("cn");
        when(authConfig.getRoleContextDn()).thenReturn("ou=groups,dc=inkstand");
        when(authConfig.getRoleFilter()).thenReturn("(uniqueMember={1})");
        //search scope
        when(authConfig.getSearchScope()).thenReturn(LdapAuthConfiguration.SearchScope.SUBTREE);

        //prepare the user login data, see ldif
        final String userId = "testuser";
        final PasswordCredential passwordCredential = new PasswordCredential("Password1".toCharArray());

        subject.connect();

        //act
        Account account = subject.verify(userId, passwordCredential);

        //assert
        assertNotNull(account);
        assertEquals(userId, account.getPrincipal().getName());
    }


}
