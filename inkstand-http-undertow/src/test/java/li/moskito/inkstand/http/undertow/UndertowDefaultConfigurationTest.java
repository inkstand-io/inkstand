package li.moskito.inkstand.http.undertow;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.inkstand.scribble.Scribble;

public class UndertowDefaultConfigurationTest {

    private UndertowDefaultConfiguration subject;

    @Before
    public void setUp() throws Exception {
        subject = new UndertowDefaultConfiguration();
        //SCRIB-7 injection of primitive types does not work
        Scribble.inject(1024).asConfigProperty("inkstand.http.port").into(subject);
        Scribble.inject("foreign.host").asConfigProperty("inkstand.http.listenaddress").into(subject);
    }

    //TODO reenable test
    @Test
    @Ignore
    public void testGetPort() throws Exception {
        assertEquals(1024, subject.getPort());
    }

    @Test
    public void testGetBindAddress() throws Exception {
        assertEquals("foreign.host", subject.getBindAddress());
    }

}
