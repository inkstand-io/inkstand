/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.mgmt;

import static io.inkstand.MicroService.StateSupport.State.RUNNING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.inkstand.MicroServiceController;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.MockUtil;
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
    private MicroServiceController msc;

    private ServletOutputStreamAnswer outStream;

    @Before
    public void setUp() throws Exception {

        AttributeAnswer.mockAttributes(req);
        outStream = ServletOutputStreamAnswer.mockOutputStream(res);
        subject.init(servletConfig);
    }

    @After
    public void tearDown() throws Exception {
        subject.destroy();
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
    public void testGetServletInfo() throws Exception {

        //prepare

        //act
        String info = subject.getServletInfo();

        //assert
        assertNotNull(info);
        assertEquals("Inkstand Management Servlet", info);
    }

    @Test
    public void testService_optionsMethod() throws Exception {
        //prepare
        when(req.getMethod()).thenReturn("OPTIONS");

        //act
        subject.service(req, res);

        //assert
        verify(res).setHeader("Allow", "GET, HEAD, POST, TRACE, OPTIONS");

    }

    @Test
    public void testService_get_jsonAccepted_statusResource() throws Exception {
        //prepare
        when(req.getMethod()).thenReturn("GET");
        when(req.getHeader("Accept")).thenReturn("application/json");
        when(req.getPathInfo()).thenReturn("/status/");
        setupBeanManager();
        when(msc.getState()).thenReturn(RUNNING);

        //act
        subject.service(req, res);

        //assert
        verify(res).setContentType("application/json");
        JsonObject object = Json.createReader(outStream.getBytesAsStream()).readObject();
        assertEquals("RUNNING", object.getString("state"));
    }

    private void setupBeanManager() {

        BeanManager bm = CdiContainerLoader.getCdiContainer().getBeanManager();
        final Bean msBean = mock(Bean.class);
        final Set<Bean<?>> beans = new HashSet<>(Arrays.<Bean<?>>asList(msBean));
        when(bm.getBeans(MicroServiceController.class)).thenReturn(beans);
        when(bm.resolve(beans)).thenReturn(msBean);
        when(bm.getReference(eq(msBean), any(Type.class), any(CreationalContext.class))).thenReturn(msc);
    }

    @Test
    public void testService_post_jsonAccepted_shutdownResource() throws Exception {
        //prepare
        int delay = 0;
        when(req.getMethod()).thenReturn("POST");
        when(req.getHeader("Accept")).thenReturn("application/json");
        when(req.getPathInfo()).thenReturn("/shutdown/");
        setupBeanManager();
        ServletInputStreamAnswer.mockInputStream(req).setData("{\"delay\":"+delay+"}");

        //act
        subject.service(req, res);

        //assert
        verify(res).setContentType("application/json");
        JsonObject object = Json.createReader(outStream.getBytesAsStream()).readObject();
        assertEquals("Shutdown request received", object.getString("msg"));
        Thread.sleep(delay * 1000 + 100);
        MockCdiContainer cdi = (MockCdiContainer) CdiContainerLoader.getCdiContainer();
        assertTrue(cdi.isShutdown());

    }

    @Test
    public void testService_get_jsonAccepted_wrongResourcePath_404() throws Exception {
        //prepare
        when(req.getMethod()).thenReturn("GET");
        when(req.getHeader("Accept")).thenReturn("application/json");
        when(req.getPathInfo()).thenReturn("/");

        //act
        subject.service(req, res);

        //assert
        verify(res).setContentType("application/json");
        verify(res).setStatus(404);
        JsonObject object = Json.createReader(outStream.getBytesAsStream()).readObject();
        assertEquals("Resource not found", object.getString("message"));
        assertEquals("/", object.getString("resource"));
    }

    @Test
    public void testService_post_jsonAccepted_wrongResourcePath_404() throws Exception {
        //prepare
        when(req.getMethod()).thenReturn("POST");
        when(req.getHeader("Accept")).thenReturn("application/json");
        when(req.getPathInfo()).thenReturn("/");
        ServletInputStreamAnswer.mockInputStream(req).setData("{\"name\":\"value\"}");

        //act
        subject.service(req, res);

        //assert
        verify(res).setContentType("application/json");
        verify(res).setStatus(404);
        JsonObject object = Json.createReader(outStream.getBytesAsStream()).readObject();
        assertEquals("Resource not found", object.getString("message"));
        assertEquals("/", object.getString("resource"));
    }

    @Test
     public void testService_get_jsonNotAccepted_406() throws Exception {
        //prepare
        when(req.getMethod()).thenReturn("GET");
        when(req.getHeader("Accept")).thenReturn("application/pdf");
        when(req.getPathInfo()).thenReturn("/");

        //act
        subject.service(req, res);

        //assert
        verify(res).setContentType("application/json");
        verify(res).setStatus(406);
        JsonObject object = Json.createReader(outStream.getBytesAsStream()).readObject();
        assertEquals("ContentType: application/pdf not supported", object.getString("message"));
    }

    @Test
    public void testService_get_noAcceptHeader_statusResource() throws Exception {
        //prepare
        when(req.getMethod()).thenReturn("GET");
        when(req.getPathInfo()).thenReturn("/status/");
        setupBeanManager();
        when(msc.getState()).thenReturn(RUNNING);

        //act
        subject.service(req, res);

        //assert
        verify(res).setContentType("application/json");
        JsonObject object = Json.createReader(outStream.getBytesAsStream()).readObject();
        assertEquals("RUNNING", object.getString("state"));
    }

    @Test
    public void testService_post_jsonNotAccepted_406() throws Exception {
        //prepare
        when(req.getMethod()).thenReturn("GET");
        when(req.getHeader("Accept")).thenReturn("application/pdf");
        when(req.getPathInfo()).thenReturn("/");
        ServletInputStreamAnswer.mockInputStream(req).setData("{\"name\":\"value\"}");

        //act
        subject.service(req, res);

        //assert
        verify(res).setContentType("application/json");
        verify(res).setStatus(406);
        JsonObject object = Json.createReader(outStream.getBytesAsStream()).readObject();
        assertEquals("ContentType: application/pdf not supported", object.getString("message"));
    }


    /**
     * Answer for mocking a servlet response. The outStream may be used to mock a ServletResponse by partially
     * implementing its write method to write into a byte array that can be read afterward.
     */
    private static class ServletInputStreamAnswer implements Answer<Object> {

        //TODO candidate for scribble-servlet
        private InputStream input;

        @Override
        public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {

            assertNotNull("Answer not initialized", input);
            if (InputStream.class.getMethod("read").equals(invocationOnMock.getMethod())) {
                return input.read();
            }

            return invocationOnMock.callRealMethod();
        }

        public void setData(byte[] data) {

            input = new ByteArrayInputStream(data);
        }

        public void setData(InputStream is) {

            input = is;
        }

        public void setData(String data) {

            setData(data.getBytes());
        }

        public static ServletInputStreamAnswer mockInputStream(ServletRequest req) {

            assertTrue(req + " is not mock", new MockUtil().isMock(req));
            final ServletInputStream srvIn = mock(ServletInputStream.class);
            final ServletInputStreamAnswer answer = new ServletInputStreamAnswer();
            try {
                when(req.getInputStream()).thenReturn(srvIn);
                doAnswer(answer).when(srvIn).available();
                doAnswer(answer).when(srvIn).read();
                doAnswer(answer).when(srvIn).read(any(byte[].class));
                doAnswer(answer).when(srvIn).read(any(byte[].class), anyInt(), anyInt());
            } catch (IOException e) {
                throw new AssertionError("Unexpected exception in mocking", e);
            }
            return answer;
        }
    }

    /**
     * Answer for mocking a servlet response. The outStream may be used to mock a ServletResponse by partially
     * implementing its write method to write into a byte array that can be read afterward.
     */
    private static class ServletOutputStreamAnswer implements Answer<Object> {

        //TODO candidate for scribble-servlet
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        @Override
        public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {

            if (OutputStream.class.getMethod("write", int.class).equals(invocationOnMock.getMethod())) {
                out.write((int) invocationOnMock.getArguments()[0]);
                return null;
            }
            return invocationOnMock.callRealMethod();
        }

        /**
         * @return the bytes written to the servlet response
         */
        public byte[] getBytes() {

            return out.toByteArray();
        }

        /**
         * @return the bytes written to the servlet response as stream
         */
        public InputStream getBytesAsStream() {

            return new ByteArrayInputStream(out.toByteArray());
        }

        public static ServletOutputStreamAnswer mockOutputStream(ServletResponse res) {

            assertTrue(res + " is not mock", new MockUtil().isMock(res));
            final ServletOutputStream srvOut = mock(ServletOutputStream.class);
            final ServletOutputStreamAnswer answer = new ServletOutputStreamAnswer();
            try {
                when(res.getOutputStream()).thenReturn(srvOut);
                doAnswer(answer).when(srvOut).write(anyInt());
                doAnswer(answer).when(srvOut).write(any(byte[].class));
                doAnswer(answer).when(srvOut).write(any(byte[].class), anyInt(), anyInt());
            } catch (IOException e) {
                throw new AssertionError("Unexpected exception in mocking", e);
            }
            return answer;
        }
    }

    /**
     * Answer for mocking the attribute handling of a servlet request.
     */
    private static class AttributeAnswer implements Answer<Object> {

        //TODO candidate for scribble-servlet

        private Map<String, Object> attributes = new HashMap<>();

        @Override
        public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {

            if ("setAttribute".equals(invocationOnMock.getMethod().getName())) {
                attributes.put((String) invocationOnMock.getArguments()[0], invocationOnMock.getArguments()[1]);
                return null;
            } else if ("getAttribute".equals(invocationOnMock.getMethod().getName())) {
                return attributes.get(invocationOnMock.getArguments()[0]);
            } else if ("getAttributeNames".equals(invocationOnMock.getMethod().getName())) {
                return Collections.enumeration(attributes.keySet());
            } else {
                throw new AssertionError(invocationOnMock.getMethod() + " is not supported by this outStream");
            }
        }

        /**
         * @return the attribute map of recored attributes. Can be modified to induce a certain state for the request.
         */
        public Map<String, Object> getAttributes() {

            return attributes;
        }

        /**
         * Adds attribute mocking to the servlet request mock
         *
         * @param req
         *         the servlet request mock to be enriched with attribute handling
         *
         * @return the servlet request mock with attribute handling
         */
        public static AttributeAnswer mockAttributes(ServletRequest req) {

            assertTrue(req + " is not mock", new MockUtil().isMock(req));
            final AttributeAnswer answer = new AttributeAnswer();
            doAnswer(answer).when(req).setAttribute(anyString(), any(Object.class));
            doAnswer(answer).when(req).getAttribute(anyString());
            doAnswer(answer).when(req).getAttributeNames();
            return answer;
        }
    }
}
