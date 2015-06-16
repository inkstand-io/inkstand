package io.inkstand.http.undertow.auth.ldap;

import static io.inkstand.scribble.Scribble.inject;
import static io.inkstand.security.LdapAuthConfiguration.SearchScope.BASE;
import static io.inkstand.security.LdapAuthConfiguration.SearchScope.SUBTREE;
import static org.junit.Assert.assertEquals;

import io.inkstand.security.LdapAuthConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Gerald on 29.05.2015.
 */
public class DefaultLdapConfigurationTest {

    /**
     * The class under test
     */
    private DefaultLdapConfiguration subject;

    @Before
    public void setUp() throws Exception {
        subject = new DefaultLdapConfiguration();
    }


    @Test

    public void testGetPort_defaultPort() throws Exception {

        //prepare
        inject(null).asConfigProperty("inkstand.auth.ldap.port").into(subject);

        //act
        int port = subject.getPort();

        //assert
        assertEquals(389, port);
    }@Test
    public void testGetPort_configuredPort() throws Exception {

        //prepare
        inject(10389).asConfigProperty("inkstand.auth.ldap.port").into(subject);

        //act
        int port = subject.getPort();

        //assert
        assertEquals(10389, port);
    }

    @Test
    public void testGetHostname_defaultHostname() throws Exception {

        //prepare
        inject(null).asConfigProperty("inkstand.auth.ldap.host").into(subject);

        //act
        String hostname = subject.getHostname();

        //assert
        assertEquals("localhost", hostname);
    }

    @Test
    public void testGetHostname_configuredHostname() throws Exception {

        //prepare
        inject("ldapserver").asConfigProperty("inkstand.auth.ldap.host").into(subject);

        //act
        String hostname = subject.getHostname();

        //assert
        assertEquals("ldapserver", hostname);
    }

    @Test
    public void testGetBindDn() throws Exception {

        //prepare
        inject("uid=bind").asConfigProperty("inkstand.auth.ldap.bind.dn").into(subject);

        //act
        String bindDn = subject.getBindDn();

        //assert
        assertEquals("uid=bind", bindDn);
    }

    @Test
    public void testGetBindCredentials() throws Exception {
        //prepare
        inject("secret").asConfigProperty("inkstand.auth.ldap.bind.credentials").into(subject);

        //act
        String credentials = subject.getBindCredentials();

        //assert
        assertEquals("secret", credentials);
    }

    @Test
    public void testGetUserContextDn() throws Exception {
        //prepare
        inject("ou=users").asConfigProperty("inkstand.auth.ldap.user.context.dn").into(subject);

        //act
        String userContextDn = subject.getUserContextDn();

        //assert
        assertEquals("ou=users", userContextDn);
    }

    @Test
    public void testGetUserFilter() throws Exception {
        //prepare
        inject("(uid=?)").asConfigProperty("inkstand.auth.ldap.user.filter").into(subject);

        //act
        String userFilter = subject.getUserFilter();

        //assert
        assertEquals("(uid=?)", userFilter);
    }

    @Test
    public void testGetRoleContextDn() throws Exception {
//prepare
        inject("ou=groups").asConfigProperty("inkstand.auth.ldap.role.context.dn").into(subject);

        //act
        String userRoleDn = subject.getRoleContextDn();

        //assert
        assertEquals("ou=groups", userRoleDn);
    }

    @Test
    public void testGetRoleFilter() throws Exception {
        //prepare
        inject("(member=?)").asConfigProperty("inkstand.auth.ldap.role.filter").into(subject);

        //act
        String roleFilter = subject.getRoleFilter();

        //assert
        assertEquals("(member=?)", roleFilter);
    }

    @Test
    public void testGetRoleNameAttribute() throws Exception {
        //prepare
        inject("cn").asConfigProperty("inkstand.auth.ldap.role.nameAttribute").into(subject);

        //act
        String roleNameAttribute = subject.getRoleNameAttribute();

        //assert
        assertEquals("cn", roleNameAttribute);
    }

    @Test
    public void testGetSearchScope_configuredValue() throws Exception {
        //prepare
        inject("BASE").asConfigProperty("inkstand.auth.ldap.searchScope").into(subject);

        //act
        LdapAuthConfiguration.SearchScope searchScope = subject.getSearchScope();

        //assert
        assertEquals(BASE, searchScope);
    }
    @Test
    public void testGetSearchScope_defaultValue() throws Exception {
        //prepare
        inject(null).asConfigProperty("inkstand.auth.ldap.searchScope").into(subject);

        //act
        LdapAuthConfiguration.SearchScope searchScope = subject.getSearchScope();

        //assert
        assertEquals(SUBTREE, searchScope);
    }
}
