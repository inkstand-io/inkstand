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

package io.inkstand.it;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.inkstand.Inkstand;
import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.net.NetworkUtils;
import io.inkstand.scribble.rules.TemporaryFile;

/**
 * Test case for testing serving of static content provided in a zip file.
 * Created by Gerald on 26.07.2015.
 */
public class StaticContentITCase {

    @ClassRule
    public final static TemporaryFolder folder = Scribble.newTempFolder().build();

    @Rule
    public final TemporaryFile file = Scribble.newTempFolder().aroundTempFile("index.html")
                                                     .withContent()
                                                     .fromClasspathResource("/index.html")
                                                     .build();

    public static File contentFile;

    private int port;

    private Properties originalProperties;


    @BeforeClass
    public static void setupTestContent() throws IOException {

        contentFile = createZip(folder.newFile("content.zip"), "/index.html", "/img/image.png");

    }

    @Before
    public void setUp() throws Exception {
        originalProperties = System.getProperties();
        port = NetworkUtils.findAvailablePort();
        //we set the port to use a randomized port for testing, otherwise the default port 80 will be used
        System.setProperty("inkstand.http.port", String.valueOf(port));

    }

    @After
    public void tearDown() throws Exception {
        System.setProperties(originalProperties);
    }

    private static File createZip(final File file, final String... resources) throws IOException {

        try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream((file)))) {

            for(String resourceName  : resources){

                final InputStream is = StaticContentITCase.class.getResourceAsStream(resourceName);
                final ZipEntry zipEntry = new ZipEntry(resourceName);
                zos.putNextEntry(zipEntry);
                IOUtils.copy(is, zos);
                zos.closeEntry();
            }
        }
        return file;
    }

    @Test
    public void testGetStaticContent_fromZipFile_indexFile_bySystemProperty() throws IOException {
        //prepare
        System.setProperty("inkstand.http.content.root", contentFile.getAbsolutePath());

        //act
        Inkstand.main(new String[] {});

        //assert
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage("http://localhost:"+port+"/index.html");

            final String pageAsText = page.asText();
            assertTrue(pageAsText.contains("Static Content Test"));
        }
    }

    @Test
    public void testGetStaticContent_fromZipFile_indexFile_byCmdLineArgs() throws IOException {
        //prepare
        String[] args = new String[]{"-contentRoot", contentFile.getAbsolutePath()};

        //act
        Inkstand.main(args);

        //assert
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage("http://localhost:"+port+"/index.html");

            final String pageAsText = page.asText();
            assertTrue(pageAsText.contains("Static Content Test"));
        }
    }

    @Test
    public void testGetStaticContent_fromFilesystem_indexFile_bySystemProperty() throws IOException {
        //prepare
        System.setProperty("inkstand.http.content.root", file.getFile().getParentFile().getAbsolutePath());

        //act
        Inkstand.main(new String[] {});

        //assert
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage("http://localhost:"+port+"/index.html");

            final String pageAsText = page.asText();
            assertTrue(pageAsText.contains("Static Content Test"));
        }
    }

    @Test
    public void testGetStaticContent_fromFilesystem_indexFile_byCmdLineArgs() throws IOException {
        //prepare
        String[] args = new String[]{"-contentRoot", file.getFile().getParentFile().getAbsolutePath()};

        //act
        Inkstand.main(args);

        //assert
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage("http://localhost:"+port+"/index.html");

            final String pageAsText = page.asText();
            assertTrue(pageAsText.contains("Static Content Test"));
        }
    }


}
