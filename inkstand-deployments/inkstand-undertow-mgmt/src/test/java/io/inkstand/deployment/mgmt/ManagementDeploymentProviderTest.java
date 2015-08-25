package io.inkstand.deployment.mgmt;

import static io.inkstand.scribble.Scribble.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @Test
    public void testGetDeployment_twoServlets() throws Exception {
        //prepare
        when(webServlets.getServlets(Management.class))
                .thenReturn(this.<Class>setFromList(Servlet1.class, Servlet2.class));

        //act
        DeploymentInfo di = subject.getDeployment();

        //assert
        assertNotNull(di);
        assertEquals("Management Console", di.getDeploymentName());
        assertEquals("/mgmt", di.getContextPath());
        final Map<String, ServletInfo> servlets = di.getServlets();
        assertNotNull(servlets);
        assertEquals(2, servlets.size());
        assertTrue(servlets.containsKey("servlet1"));
        assertTrue(servlets.containsKey("servlet2"));

    }

    public <TYPE> Set<TYPE> setFromList(TYPE... elements) {

        final Set<TYPE> set = new HashSet<>();
        Collections.addAll(set, elements);
        return set;
    }

    @WebServlet(name = "servlet1",
                asyncSupported = true,
                loadOnStartup = 5,
                urlPatterns = { "/test1/*", "/t1/*" },
                initParams = { @WebInitParam(name="foo", value="Hello "),
                               @WebInitParam(name="bar", value=" World!")})
    public abstract class Servlet1 implements Servlet {

    }

    @WebServlet(name = "servlet2",
                asyncSupported = true,
                loadOnStartup = 10,
                urlPatterns = { "/test2/*", "/t2/*" },
                initParams = { @WebInitParam(name="text", value="Test")})
    public abstract class Servlet2 implements Servlet {

    }
}
