package li.moskito.inkstand.deployment.resteasy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.inkstand.scribble.Scribble;

import java.util.Collection;

import li.moskito.inkstand.cdi.ResourcesAndProviders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DynamicResteasyConfigurationTest {

    @Mock
    private ResourcesAndProviders scanner;

    @InjectMocks
    private DynamicResteasyConfiguration subject;

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Test
    public void testGetProviderClasses() throws Exception {
        final Collection<Class> providers = mock(Collection.class);
        when(scanner.getProviderClasses()).thenReturn(providers);
        assertEquals(providers, subject.getProviderClasses());
    }

    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Test
    public void testGetResourceClasses() throws Exception {
        final Collection<Class> resources = mock(Collection.class);
        when(scanner.getResourceClasses()).thenReturn(resources);
        assertEquals(resources, subject.getResourceClasses());
    }

    @Test
    public void testGetContextRoot_configured() throws Exception {
        Scribble.injectInto(subject).configProperty("inkstand.rest.contextRoot").value("root");
        assertEquals("root", subject.getContextRoot());
    }

    @Test
    public void testGetContextRoot_unconfigured() throws Exception {
        assertEquals("", subject.getContextRoot());
    }

}
