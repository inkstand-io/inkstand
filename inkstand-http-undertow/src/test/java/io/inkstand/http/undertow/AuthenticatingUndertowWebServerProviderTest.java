package io.inkstand.http.undertow;

import static io.inkstand.scribble.Scribble.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.config.WebServerConfiguration;
import io.undertow.Undertow;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentInfo;

/**
 * Created by Gerald on 29.05.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticatingUndertowWebServerProviderTest  {

    @Mock
    private WebServerConfiguration config;

    @Mock
    private IdentityManager identityManager;


    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DeploymentInfo deploymentInfo;

    /**
     * The class under test
     */
    @InjectMocks
    private AuthenticatingUndertowWebServerProvider subject;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        when(config.getBindAddress()).thenReturn("localhost");
        when(config.getPort()).thenReturn(41080);
        when(deploymentInfo.clone()).thenReturn(deploymentInfo);
        when(deploymentInfo.getDefaultEncoding()).thenReturn("UTF-8");
        when(deploymentInfo.getDeploymentName()).thenReturn("test.war");
        when(deploymentInfo.getContextPath()).thenReturn("test");
        when(deploymentInfo.getThreadSetupActions()).thenReturn(Collections.EMPTY_LIST);
    }


    @Test
    public void testGetLdapAuthUndertow() throws Exception {
        //prepare

        //act
        Undertow undertow = subject.getLdapAuthUndertow();


        //assert
        assertNotNull(undertow);
        verify(deploymentInfo).setIdentityManager(identityManager);
        try {
            undertow.start();
            URL url = new URL("http://localhost:41080/test");
            url.openConnection().connect();
        } finally {
            undertow.stop();
        }
    }

    @Test
    public void testAddSecurity() throws Exception {

        //prepare
        inject(null).asConfigProperty("inkstand.http.auth.realm").into(subject);
        HttpHandler noAuthHandler = mock(HttpHandler.class);

        //act
        HttpHandler authHandler = subject.addSecurity(noAuthHandler);

        //assert
        assertNotNull(authHandler);
        assertNotEquals(authHandler, noAuthHandler);
    }

    @Test
    public void testGetRealm_defaultRealm() throws Exception {
        //prepare
        inject(null).asConfigProperty("inkstand.http.auth.realm").into(subject);

        //act
        String realm = subject.getRealm();

        //assert
        assertEquals("DefaultRealm", realm);
    }


    @Test
    public void testGetRealm_configuredRealm() throws Exception {
        //prepare
        inject("myRealm").asConfigProperty("inkstand.http.auth.realm").into(subject);

        //act
        String realm = subject.getRealm();

        //assert
        assertEquals("myRealm", realm);
    }
}
