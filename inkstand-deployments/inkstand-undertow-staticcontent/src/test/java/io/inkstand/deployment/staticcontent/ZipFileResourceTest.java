/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.deployment.staticcontent;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.lang3.time.FastDateFormat;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.BlockingHttpExchange;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.util.ETag;
import io.undertow.util.MimeMappings;

/**
 * Created by Gerald on 27.07.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipFileResourceTest {

    public static final String ENTRY_PATH = "/some/path/file.pdf";

    @Mock
    private ZipFile zipFile;

    @Mock
    private ZipEntry zipEntry;

    @Mock
    private BlockingHttpExchange httpExchange;
    @Mock
    private ServerConnection serverCon;
    @Mock
    private Sender sender;
    @Mock
    private IoCallback completionCallback;


    /**
     * The class under test
     */
    private ZipFileResource subject;

    @Before
    public void setUp() throws Exception {

        when(zipFile.getEntry(ENTRY_PATH)).thenReturn(zipEntry);
        when(zipEntry.getName()).thenReturn(ENTRY_PATH);
        subject = new ZipFileResource(zipFile, ENTRY_PATH);
    }

    @Test
    public void testGetPath() throws Exception {
        //prepare

        //act
        String actualPath = subject.getPath();

        //assert
        assertEquals(ENTRY_PATH, actualPath);
    }

    @Test
    public void testGetLastModified() throws Exception {
        //prepare
        when(zipEntry.getTime()).thenReturn(1_000_000_000L);

        //act
        Date date = subject.getLastModified();

        //assert
        assertNotNull(date);
        assertEquals(new Date(1_000_000_000L), date);
    }

    @Test
    public void testGetLastModifiedString() throws Exception {


        //prepare
        when(zipEntry.getTime()).thenReturn(1_000_000_000L);

        //act
        String lmString = subject.getLastModifiedString();

        //assert
        assertNotNull(lmString);
        assertThat(lmString, matchesDate(new Date(1_000_000_000L)).formatted("EEE, dd MMM yyyy HH:mm:ss zzz"));

    }

    //TODO contribute to Scribble
    public static class DateMatcher extends BaseMatcher<String> {

        private final Date expectedDate;
        private String dateFormat;
        private String mismatch;

        public DateMatcher(Date expectedDate) {

            this.expectedDate = expectedDate;
        }

        public DateMatcher formatted(String expectedFormat) {

            this.dateFormat = expectedFormat;
            return this;
        }

        @Override
        public boolean matches(final Object item) {

            if (item instanceof Date) {
                return expectedDate.equals(item);
            } else if (item instanceof String) {
                return matchesFormattedDate((String) item);

            }
            return false;
        }

        private boolean matchesFormattedDate(final String item) {

            FastDateFormat format;
            if (dateFormat == null) {
                format = FastDateFormat.getInstance();
            } else {
                format = FastDateFormat.getInstance(dateFormat);
            }

            try {
                return expectedDate.equals(format.parse(item));
            } catch (ParseException e) {
                mismatch = e.getMessage();
                return false;
            }
        }

        @Override
        public void describeTo(final Description description) {

            description.appendText("Expected date " + expectedDate);
            if (dateFormat != null) {
                description.appendText(" in format " + dateFormat);
            }
        }

        @Override
        public void describeMismatch(final Object item, final Description description) {

            if (mismatch == null) {
                super.describeMismatch(item, description);
            } else {
                description.appendText(mismatch);
            }

        }
    }

    //TODO contribute to scribble
    protected static DateMatcher matchesDate(final Date date) {

        return new DateMatcher(date);

    }

    @Test
    public void testGetETag() throws Exception {

        //prepare

        //act
        ETag etag = subject.getETag();

        //assert
        assertNull(etag);
    }

    @Test
    public void testGetName() throws Exception {
        //prepare

        //act
        String name = subject.getName();

        //assert
        assertEquals(ENTRY_PATH, name);
    }

    @Test
    public void testIsDirectory_fileEntry_false() throws Exception {
        //prepare

        //act
        boolean isDir = subject.isDirectory();

        //assert
        assertFalse(isDir);
    }

    @Test
    public void testList_fileEntry_emptyList() throws Exception {

        //prepare
        Enumeration enumeration = Collections.enumeration(Arrays.asList(zipEntry));
        when(zipFile.entries()).thenReturn(enumeration);
        when(zipEntry.isDirectory()).thenReturn(false);

        //act
        List<Resource> resources = subject.list();

        //assert
        assertNotNull(resources);
        assertTrue(resources.isEmpty());
    }

    @Test
    public void testGetContentType_DefaultMimeMappings() throws Exception {
        //prepare

        //act
        String contentType = subject.getContentType(MimeMappings.DEFAULT);

        //assert
        assertNotNull(contentType);
        assertEquals("application/pdf", contentType);
    }

    @Test
    public void testServe_success() throws Exception {
        //prepare

        final byte[] data = "TestText".getBytes();
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(httpExchange.getOutputStream()).thenReturn(out);
        when(zipFile.getInputStream(zipEntry)).thenReturn(in);

        //simulate a blocking request
        final HttpServerExchange exchange = new HttpServerExchange(serverCon);
        exchange.startBlocking(httpExchange);

        //act
        subject.serve(sender, exchange, completionCallback);

        //assert
        assertArrayEquals(data, out.toByteArray());
        verify(completionCallback).onComplete(exchange, sender);
    }

    @Test
    public void testServe_ioException_error() throws Exception {
        //prepare
        final IOException iox = new IOException("Expected");
        when(zipFile.getInputStream(zipEntry)).thenThrow(iox);

        //simulate a blocking request
        final HttpServerExchange exchange = new HttpServerExchange(serverCon);
        exchange.startBlocking(httpExchange);

        //act
        subject.serve(sender, exchange, completionCallback);

        //assert
        verify(completionCallback, times(0)).onComplete(exchange, sender);
        verify(completionCallback).onException(exchange, sender, iox);
    }

    @Test
    public void testGetContentLength() throws Exception {

        //prepare
        when(zipEntry.getSize()).thenReturn(123L);

        //act
        Long length = subject.getContentLength();

        //assert
        assertNotNull(length);
        assertEquals(Long.valueOf(123L), length);
    }

    @Test
    public void testGetCacheKey() throws Exception {

        //prepare

        //act
        String cacheKey = subject.getCacheKey();

        //assert
        assertNull(cacheKey);
    }

    @Test
    public void testGetFile() throws Exception {
        //prepare

        //act
        File file = subject.getFile();

        //assert
        assertNull(file);
    }

    @Test
    public void testGetResourceManagerRoot() throws Exception {
        //prepare

        //act
        File rmRoot = subject.getResourceManagerRoot();

        //assert
        assertNull(rmRoot);
    }

    @Test
    public void testGetUrl() throws Exception {
        //prepare

        //act
        URL url = subject.getUrl();

        //assert
        assertNull(url);
    }
}
