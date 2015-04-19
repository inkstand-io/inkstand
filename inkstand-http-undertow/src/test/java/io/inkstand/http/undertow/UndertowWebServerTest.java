package io.inkstand.http.undertow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import io.undertow.Undertow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UndertowWebServerTest {

    @Mock
    private Undertow undertow;

    @InjectMocks
    private UndertowWebServer subject;

    @Test
    public void testStart() throws Exception {
        subject.start();
        verify(undertow).start();
    }

    @Test
    public void testStop() throws Exception {
        subject.stop();
        verify(undertow).stop();
    }

    @Test
    public void testGetUndertow() throws Exception {
        assertEquals(undertow, subject.getUndertow());
    }

}
