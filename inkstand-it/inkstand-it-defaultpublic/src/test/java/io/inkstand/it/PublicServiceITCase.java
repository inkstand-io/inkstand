package io.inkstand.it;

import javax.ws.rs.client.ClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.inkstand.Inkstand;
import io.inkstand.scribble.net.NetworkUtils;

/**
 * Created by Gerald on 27.05.2015.
 */
public class PublicServiceITCase {

    private int port;

    @Before
    public void setUp() throws Exception {
        port = NetworkUtils.findAvailablePort();
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));

    }

    @Test
    public void testGetApp() throws InterruptedException {

        //prepare
        Inkstand.main(new String[]{});

        //act
        String value = ClientBuilder.newClient()
                                    .target("http://localhost:" + port + "/test")
                                    .request().get(String.class);
        //assert
        Assert.assertEquals("test", value);
    }
}
