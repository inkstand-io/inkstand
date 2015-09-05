package io.inkstand.mgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * Created by Gerald Mücke on 25.08.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContainerControlServletTest {

    /**
     * The class under test
     */
    @InjectMocks
    private ContainerControlServlet subject;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse res;

    @Mock
    private ServletOutputStream srvOut;

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    ServletOutputStreamAnswer answer = new ServletOutputStreamAnswer();
    private JsonGenerator json;

    @Before
    public void setUp() throws Exception {

        doAnswer(answer).when(srvOut).write((byte[]) anyObject(), anyInt(), anyInt());
        doAnswer(answer).when(srvOut).write((byte[]) anyObject());
        doAnswer(answer).when(srvOut).write((anyInt()));
        when(res.getOutputStream()).thenReturn(srvOut);
        this.json = Json.createGenerator(outStream);
    }

    @Test
    @Ignore
    public void testInit_and_getServletConfig() throws Exception {

        //prepare

        //act
        subject.init(servletConfig);

        //assert
        assertEquals(servletConfig, subject.getServletConfig());
    }


    @Test
    public void testGetServletInfo() throws Exception {

        //prepare

        //act
        String info = subject.getServletInfo();

        //assert
        assertNotNull(info);
        assertEquals("Inkstand Management Servlet", info);
    }

    @Test
    public void testService_jsonAccepted() throws Exception {
        //prepare

        //act


        //assert

    }

    @Test
    public void test_GET_status() throws Exception {
        //


        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn("status");
        when(req.getAttribute("json")).thenReturn(json);

        //act
        subject.doGet(req, res);

        //assert


    }



    /**
     * Answer for mocking a servlet response. The answer may be used to mock a ServletResponse by partially implementing
     * its write method to write into a byte array that can be read afterward.
     */
    private static class ServletOutputStreamAnswer implements Answer<Object> {

        //TODO candidate for Scribble
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        @Override
        public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {

            Object[] args = invocationOnMock.getArguments();
            if(args[0] instanceof byte[]) {
                if(args.length == 3){
                    out.write((byte[]) args[0], (int)args[1], (int)args[2]);
                }else {
                    out.write((byte[]) args[0]);
                }
            } else {
                out.write((int) args[0]);
            }
            return null;
        }
    }
}
