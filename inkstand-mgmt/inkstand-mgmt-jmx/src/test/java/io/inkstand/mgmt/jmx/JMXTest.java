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

package io.inkstand.mgmt.jmx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Gerald Muecke on 06.10.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class JMXTest {

    /**
     * The class under test
     */
    @InjectMocks
    private JMX subject;

    @Test
    public void testIntrospection() throws Exception {

        //prepare

        //act
        String result = subject.introspection();

        //assert
        final JsonObject json = getJsonObject(result);
        assertFalse(json.isNull("_links"));
        final JsonObject links = json.getJsonObject("_links");
        assertRelExists(links, "runtime");
        assertRelExists(links, "memory");
        assertRelExists(links, "os");
        assertRelExists(links, "threads");
        assertRelExists(links, "classloading");
        assertRelExists(links, "compilation");
    }

    private JsonObject getJsonObject(final String result) {

        assertNotNull(result);
        return Json.createReader(new StringReader(result)).readObject();
    }

    private void assertRelExists(final JsonObject links, final String rel) {

        assertFalse(links.isNull(rel));
        assertFalse(links.getJsonObject(rel).isNull("href"));
    }

    @Test
    public void testRuntime() throws Exception {
        //prepare

        //act
        String result = subject.runtime();

        //assert
        JsonObject json = getJsonObject(result);
        assertFalse(json.isNull("bootClassPath"));
        assertFalse(json.isNull("classPath"));
        assertFalse(json.isNull("libraryPath"));
        assertFalse(json.isNull("managementSpecVersion"));
        assertFalse(json.isNull("name"));
        assertFalse(json.isNull("specName"));
        assertFalse(json.isNull("specVersion"));
        assertFalse(json.isNull("startTime"));
        assertFalse(json.isNull("upTime"));
        assertFalse(json.isNull("vmVendor"));
        assertFalse(json.isNull("vmVersion"));
        assertFalse(json.isNull("inputArgs"));
        assertFalse(json.isNull("systemProperties"));

    }

    @Test
    public void testMemory() throws Exception {
        //act
        String result = subject.memory();

        //assert
        JsonObject json = getJsonObject(result);
        assertFalse(json.isNull("objectPendingFinalizationCount"));
        assertFalse(json.isNull("heapUsage"));
        assertMemory(json.getJsonObject("heapUsage"));
        assertMemory(json.getJsonObject("nonHeapUsage"));
    }

    private void assertMemory(final JsonObject memory) {

        assertFalse(memory.isNull("init"));
        assertFalse(memory.isNull("used"));
        assertFalse(memory.isNull("max"));
        assertFalse(memory.isNull("committed"));
    }

    @Test
    public void testOperatingSystem() throws Exception {
        //act
        String result = subject.operatingSystem();

        //assert
        JsonObject json = getJsonObject(result);
        assertFalse(json.isNull("arch"));
        assertFalse(json.isNull("name"));
        assertFalse(json.isNull("version"));
        assertFalse(json.isNull("processors"));
        assertFalse(json.isNull("sysLoadAvg"));
    }

    @Test
    public void testThreads() throws Exception {
        //act
        String result = subject.threads();

        //assert
        JsonObject json = getJsonObject(result);
        assertFalse(json.isNull("currentThreadCpuTime"));
        assertFalse(json.isNull("currentThreadUserTime"));
        assertFalse(json.isNull("daemonThreadCount"));
        assertFalse(json.isNull("peakThreadCount"));
        assertFalse(json.isNull("threadCount"));
        assertFalse(json.isNull("totalStartedThreadCount"));
        assertFalse(json.isNull("threadIds"));
    }

    @Test
    public void testClassloading() throws Exception {
        //act
        String result = subject.classloading();

        //assert
        JsonObject json = getJsonObject(result);
        assertFalse(json.isNull("loadedClassCount"));
        assertFalse(json.isNull("totalLoadedClassCount"));
        assertFalse(json.isNull("totalUnloadedClassCount"));
    }

    @Test
    public void testCompilation() throws Exception {
        //act
        String result = subject.compilation();

        //assert
        JsonObject json = getJsonObject(result);
        assertFalse(json.isNull("name"));
        assertFalse(json.isNull("totalCompilationTime"));
        assertFalse(json.isNull("compilationTimeMonitoringSupported"));
    }

    @Test
    public void contract_REST_API() throws Exception {
        Path path = JMX.class.getAnnotation(Path.class);
        assertNotNull("base resource path not set", path);
        assertEquals("base resource path mismatch", "/jmx", path.value());
        assertResource(GET.class, "/runtime", "application/json", JMX.class.getMethod("runtime"));
        assertResource(GET.class, "/memory", "application/json", JMX.class.getMethod("memory"));
        assertResource(GET.class, "/os", "application/json", JMX.class.getMethod("operatingSystem"));
        assertResource(GET.class, "/threads", "application/json", JMX.class.getMethod("threads"));
        assertResource(GET.class, "/classloading", "application/json", JMX.class.getMethod("classloading"));
        assertResource(GET.class, "/compilation", "application/json", JMX.class.getMethod("compilation"));
    }

    private void assertResource(final Class<? extends Annotation> expectedAction,
                                final String expectedPath,
                                final String expectedMimetype,
                                final Method method) {

        assertAction(method, expectedAction);
        assertPath(method, expectedPath);
        assertProduces(method, expectedMimetype);
    }

    private void assertPath(final Method method, final String subpath) {

        Path path = method.getAnnotation(Path.class);
        assertNotNull("method " + method + " does not declare a subpath", path);
        assertEquals(subpath, path.value());

    }

    private void assertProduces(final Method method, final String... mimeType) {

        Produces produces = method.getAnnotation(Produces.class);
        assertNotNull("method " + method + " does not declare mimetype", produces);
        List<String> mimeTypes = Arrays.asList(produces.value());
        for(String type : mimeType) {
            assertTrue(mimeTypes.contains(type));
        }
    }

    private void assertAction(final Method method, Class<? extends Annotation> actionAnnotation) {

        assertNotNull(method.getAnnotation(actionAnnotation));
    }
}
