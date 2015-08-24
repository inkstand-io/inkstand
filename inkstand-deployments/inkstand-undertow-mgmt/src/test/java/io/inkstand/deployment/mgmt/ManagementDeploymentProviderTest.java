package io.inkstand.deployment.mgmt;

import static io.inkstand.scribble.Scribble.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import io.inkstand.Management;
import io.inkstand.cdi.WebServlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Gerald Mücke on 24.08.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ManagementDeploymentProviderTest {

    /**
     * The class under test
     */
    @InjectMocks
    private ManagementDeploymentProvider subject;

    @Mock
    private WebServlets webServlets;

    @Before
    public void setUp() throws Exception {
        inject(webServlets).asQualifyingInstance().into(subject);
        when(webServlets.getServlets(Management.class)).thenReturn(Collections.<Class>emptySet());
    }

    @Test
    public void testGetDeployment_noServlets() throws Exception {
        //prepare

        //act
        DeploymentInfo di = subject.getDeployment();

        //assert
        assertNotNull(di);
        assertEquals("Management Console", di.getDeploymentName());
        assertEquals("/mgmt", di.getContextPath());
        Map<String, ServletInfo> servlets = di.getServlets();
        assertNotNull(servlets);
        assertTrue(servlets.isEmpty());
    }
}
