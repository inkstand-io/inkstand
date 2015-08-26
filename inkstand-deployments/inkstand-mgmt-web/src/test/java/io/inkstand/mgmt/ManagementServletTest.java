package io.inkstand.mgmt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * Created by Gerald Mücke on 25.08.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ManagementServletTest  {

    /**
     * The class under test
     */
    @InjectMocks
    private ManagementServlet subject;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletRequest req;

    @Mock
    private ServletResponse res;

    @Mock
    private ServletOutputStream srvOut;

    ServletOutputStreamAnswer answer = new ServletOutputStreamAnswer();

    @Before
    public void setUp() throws Exception {

        doAnswer(answer).when(srvOut).write((byte[]) anyObject(), anyInt(), anyInt());
        doAnswer(answer).when(srvOut).write((byte[]) anyObject());
        doAnswer(answer).when(srvOut).write((anyInt()));
        when(res.getOutputStream()).thenReturn(srvOut);
    }

    @Test
    public void testInit_and_getServletConfig() throws Exception {

        //prepare

        //act
        subject.init(servletConfig);

        //assert
        assertEquals(servletConfig, subject.getServletConfig());
    }



    @Test
    public void testService() throws Exception {
        //prepare

        //act
        subject.service(req, res);

        //assert
        String response = new String(answer.out.toByteArray());

        String expected = "{\"msg\":\"ok\"}";
        JSONAssert.assertEquals(expected, response, false);

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

    private static class ServletOutputStreamAnswer implements Answer<Object> {

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
