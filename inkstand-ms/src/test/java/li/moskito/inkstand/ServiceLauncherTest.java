package li.moskito.inkstand;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServiceLauncherTest {

    @Mock
    private MicroService ms;

    @InjectMocks
    private ServiceLauncher subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() throws Exception {
        this.subject.init();
        verify(ms).start();
    }

    @Test
    public void testWatch() throws Exception {
        this.subject.watch(mock(ContainerInitialized.class));
        // nothing happens here, watch is just for the Standalone Weld Launcher
    }

    @Test
    public void testShutdown() throws Exception {
        this.subject.shutdown();
        verify(ms).stop();
    }

}
