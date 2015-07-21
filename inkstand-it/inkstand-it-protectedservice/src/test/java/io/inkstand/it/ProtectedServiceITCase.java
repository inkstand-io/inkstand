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

package io.inkstand.it;

import static io.inkstand.scribble.Scribble.newDirectory;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.ClientBuilder;
import java.net.URL;

import io.inkstand.Inkstand;
import io.inkstand.scribble.net.NetworkUtils;
import io.inkstand.scribble.rules.ldap.DirectoryServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Gerald on 27.05.2015.
 */
public class ProtectedServiceITCase {

    private int port;
    private int ldapPort;

    private static final URL ldif = ProtectedServiceITCase.class.getResource("/testusers.ldif");


    @Rule
    public final DirectoryServer ldapServer = newDirectory().withPartition("inkstand", "dc=inkstand")
                                                            .importLdif(ldif)
                                                            .aroundDirectoryServer().onAvailablePort().build();

    @Before
    public void setUp() throws Exception {

        port = NetworkUtils.findAvailablePort();
        ldapPort = ldapServer.getTcpPort();
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));
        //configuration for connecting to the LDAP server
        System.setProperty("inkstand.auth.ldap.port", String.valueOf(ldapPort));
        System.setProperty("inkstand.auth.ldap.bind.dn", "uid=admin,ou=system");
        System.setProperty("inkstand.auth.ldap.bind.credentials", "secret");
        System.setProperty("inkstand.auth.ldap.user.context.dn", "ou=users,dc=inkstand");
        System.setProperty("inkstand.auth.ldap.user.filter", "(uid={0})");
        System.setProperty("inkstand.auth.ldap.role.context.dn", "ou=groups,dc=inkstand");
        System.setProperty("inkstand.auth.ldap.role.filter", "(uniqueMember={1})");
        System.setProperty("inkstand.auth.ldap.role.nameAttribute", "cn");
        System.setProperty("inkstand.auth.ldap.searchScope", "SUBTREE");

        System.setProperty("inkstand.http.auth.securityRoles", "admins");
        System.setProperty("inkstand.http.auth.allowedRoles", "admins");
        System.setProperty("inkstand.http.auth.protectedResources", "/*");

    }

    @Test(expected = NotAuthorizedException.class)
    public void get_withoutUserAutentication_fails() throws InterruptedException {

        //prepare
        Inkstand.main(new String[] {});

        //act
        String value = ClientBuilder.newClient()
                                    .target("http://localhost:" + port + "/test")
                                    .request()
                                    .get(String.class);
    }

    @Test(expected = NotAuthorizedException.class)
    public void get_withAuthorizedUser_withWrongRole_fails() throws InterruptedException {

        //prepare
        Inkstand.main(new String[] {});

        //act
        String value = ClientBuilder.newClient()
                                    .register(new ClientAuthenticator("testuser", "Password1"))
                                    .target("http://localhost:" + port + "/test")
                                    .request()
                                    .get(String.class);
        //assert
        assertEquals("test", value);

    }

    @Test
    public void get_withAuthorizedUser_withAdminRole_succeeds() throws InterruptedException {

        //prepare
        Inkstand.main(new String[] {});

        //act
        String value = ClientBuilder.newClient()
                                    .register(new ClientAuthenticator("testadmin", "Password1"))
                                    .target("http://localhost:" + port + "/test")
                                    .request()
                                    .get(String.class);
        //assert
        assertEquals("test", value);

    }
}
