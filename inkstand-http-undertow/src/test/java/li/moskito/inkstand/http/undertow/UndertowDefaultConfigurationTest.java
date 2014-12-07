package li.moskito.inkstand.http.undertow;

import static org.junit.Assert.assertEquals;
import li.moskito.scribble.Scribble;

import org.junit.Before;
import org.junit.Test;

public class UndertowDefaultConfigurationTest {

    private UndertowDefaultConfiguration subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new UndertowDefaultConfiguration();
        Scribble.injectInto(subject).configProperty("inkstand.http.port", 1024);
        Scribble.injectInto(subject).configProperty("inkstand.http.listenaddress", "foreign.host");
    }

    @Test
    public void testGetPort() throws Exception {
        assertEquals(1024, subject.getPort());
    }

    @Test
    public void testGetBindAddress() throws Exception {
        assertEquals("foreign.host", subject.getBindAddress());
    }

}
