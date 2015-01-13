package li.moskito.inkstand.http.undertow.auth.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.Principal;
import java.util.Arrays;

import org.junit.After;
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

    @After
    public void tearDown() throws Exception {
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
        assertEquals(TEST_USER_DN, subject.getDn());
    }

}
