package li.moskito.inkstand.deployment.resteasy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import io.undertow.servlet.api.DeploymentInfo;

import java.util.Collections;

import li.moskito.inkstand.config.ApplicationConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultResteasyDeploymentProviderTest {

    @Mock
    private ApplicationConfiguration config;

    @InjectMocks
    private DefaultResteasyDeploymentProvider subject;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        when(config.getContextRoot()).thenReturn("test");
        when(config.getProviderClasses()).thenReturn(Collections.EMPTY_LIST);
        when(config.getResourceClasses()).thenReturn(Collections.EMPTY_LIST);
    }

    @Test
    public void testGetDeployment() throws Exception {
        DeploymentInfo di = this.subject.getDeployment();
        assertNotNull(di);
        assertEquals("test", di.getContextPath());
        assertEquals("ResteasyUndertow", di.getDeploymentName());
    }

}
