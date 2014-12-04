package li.moskito.inkstand.http.undertow;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;

import java.net.URL;
import java.util.Collections;

import li.moskito.inkstand.config.WebServerConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UndertowWebServerProviderTest {

    @Mock
    private WebServerConfiguration config;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DeploymentInfo deploymentInfo;

    @InjectMocks
    private UndertowWebServerProvider subject;

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
    public void testGetUndertow() throws Exception {
        Undertow undertow = subject.getUndertow();
        assertNotNull(undertow);
        try {
            undertow.start();
            URL url = new URL("http://localhost:41080/test");
            url.openConnection().connect();
        } finally {
            undertow.stop();
        }

    }

}
