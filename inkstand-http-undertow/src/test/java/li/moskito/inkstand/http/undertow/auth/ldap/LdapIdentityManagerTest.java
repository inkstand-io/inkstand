package li.moskito.inkstand.http.undertow.auth.ldap;

import static org.mockito.Mockito.when;

import org.junit.After;
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
import li.moskito.inkstand.security.LdapAuthConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class LdapIdentityManagerTest {

    @Mock
    private LdapAuthConfiguration authConfig;

    @InjectMocks
    private LdapIdentityManager subject;

    public TemporaryFolder folder = new TemporaryFolder();
    public Directory directory = new Directory(folder);
    public DirectoryServer ldapServer = new DirectoryServer(directory);
    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(folder).around(directory).around(ldapServer);

    @Before
    public void setUp() throws Exception {
        when(authConfig.getHostname()).thenReturn("localhost");
    }

    @After
    public void tearDown() throws Exception {
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
