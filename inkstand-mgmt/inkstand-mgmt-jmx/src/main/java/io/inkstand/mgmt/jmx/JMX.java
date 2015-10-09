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

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Map;

import io.inkstand.Management;

/**
 * Created by Gerald Mücke on 22.09.2015.
 */
@Management
@Path("/jmx")
public class JMX {

    public static final String PATH_RUNTIME = "/runtime";
    public static final String PATH_MEMORY = "/memory";
    public static final String PATH_OS = "/os";
    public static final String PATH_THREADS = "/threads";
    public static final String PATH_CLASSLOADING = "/classloading";
    public static final String PATH_COMPILATION = "/compilation";

    @GET
    @Produces(APPLICATION_JSON)
    public String introspection() {

        return createObjectBuilder().add("_links", createLinks()).build().toString();
    }

    private JsonObjectBuilder createLinks() {
        final JsonObjectBuilder links = createObjectBuilder();
        links.add("runtime", crateHref(PATH_RUNTIME));
        links.add("memory", crateHref(PATH_MEMORY));
        links.add("os", crateHref(PATH_OS));
        links.add("threads", crateHref(PATH_THREADS));
        links.add("classloading", crateHref(PATH_CLASSLOADING));
        links.add("compilation", crateHref(PATH_COMPILATION));
        return links;
    }

    private JsonObjectBuilder crateHref(final String href) {
        final JsonObjectBuilder builder = createObjectBuilder();
        builder.add("href", href);
        return builder;
    }

    @GET
    @Path(PATH_RUNTIME)
    @Produces(APPLICATION_JSON)
    public String runtime() {
        final JsonObjectBuilder builder = createObjectBuilder();
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        builder.add("bootClassPath", runtime.getBootClassPath());
        builder.add("classPath", runtime.getClassPath());
        builder.add("libraryPath", runtime.getLibraryPath());
        builder.add("managementSpecVersion", runtime.getManagementSpecVersion());
        builder.add("name", runtime.getName());
        builder.add("specName", runtime.getSpecName());
        builder.add("specVersion", runtime.getSpecVersion());
        builder.add("startTime", runtime.getStartTime());
        builder.add("upTime", runtime.getUptime());
        builder.add("vmName", runtime.getVmName());
        builder.add("vmVendor", runtime.getVmVendor());
        builder.add("vmVersion", runtime.getVmVersion());
        builder.add("inputArgs", getInputArgs(runtime));
        builder.add("systemProperties", getSystemProperties(runtime));
        return builder.build().toString();
    }

    private JsonArrayBuilder getInputArgs(final RuntimeMXBean runtime) {

        final JsonArrayBuilder inputArgs = createArrayBuilder();
        for(String inputArg : runtime.getInputArguments()) {
            inputArgs.add(inputArg);
        }
        return inputArgs;
    }

    private JsonObjectBuilder getSystemProperties(final RuntimeMXBean runtime) {

        final JsonObjectBuilder sysProps = createObjectBuilder();
        for(Map.Entry<String, String> sysprop : runtime.getSystemProperties().entrySet()){
            sysProps.add(sysprop.getKey(), sysprop.getValue());
        }
        return sysProps;
    }

    @GET
    @Path(PATH_MEMORY)
    @Produces(APPLICATION_JSON)
    public String memory() {
        final JsonObjectBuilder builder = createObjectBuilder();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        builder.add("objectPendingFinalizationCount", memory.getObjectPendingFinalizationCount());
        builder.add("heapUsage", toJsonObject(memory.getHeapMemoryUsage()));
        builder.add("nonHeapUsage", toJsonObject(memory.getHeapMemoryUsage()));
        return builder.build().toString();
    }

    private JsonObjectBuilder toJsonObject(final MemoryUsage memUsage) {
        final JsonObjectBuilder builder = createObjectBuilder();
        builder.add("init", memUsage.getInit());
        builder.add("used", memUsage.getUsed());
        builder.add("max", memUsage.getMax());
        builder.add("committed", memUsage.getCommitted());
        return builder;
    }

    @GET
    @Path(PATH_OS)
    @Produces(APPLICATION_JSON)
    public String operatingSystem() {
        final JsonObjectBuilder builder = createObjectBuilder();
        OperatingSystemMXBean osmxb = ManagementFactory.getOperatingSystemMXBean();
        builder.add("arch", osmxb.getArch());
        builder.add("name", osmxb.getName());
        builder.add("version", osmxb.getVersion());
        builder.add("processors", osmxb.getAvailableProcessors());
        builder.add("sysLoadAvg", osmxb.getSystemLoadAverage());
        return builder.build().toString();
    }

    @GET
    @Path(PATH_THREADS)
    @Produces(APPLICATION_JSON)
    public String threads() {
        final JsonObjectBuilder builder = createObjectBuilder();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        builder.add("currentThreadCpuTime", threads.getCurrentThreadCpuTime());
        builder.add("currentThreadUserTime", threads.getCurrentThreadUserTime());
        builder.add("daemonThreadCount", threads.getDaemonThreadCount());
        builder.add("peakThreadCount", threads.getPeakThreadCount());
        builder.add("threadCount", threads.getThreadCount());
        builder.add("totalStartedThreadCount", threads.getTotalStartedThreadCount());
        builder.add("threadIds", getThreadIds(threads));

        return builder.build().toString();
    }

    private JsonArrayBuilder getThreadIds(final ThreadMXBean threads) {

        final JsonArrayBuilder threadIds = createArrayBuilder();
        for(long threadId : threads.getAllThreadIds()){
            threadIds.add(threadId);
        }
        return threadIds;
    }

    @GET
    @Path(PATH_CLASSLOADING)
    @Produces(APPLICATION_JSON)
    public String classloading() {
        final JsonObjectBuilder builder = createObjectBuilder();
        ClassLoadingMXBean clmxb = ManagementFactory.getClassLoadingMXBean();
        builder.add("loadedClassCount", clmxb.getLoadedClassCount());
        builder.add("totalLoadedClassCount", clmxb.getTotalLoadedClassCount());
        builder.add("totalUnloadedClassCount", clmxb.getUnloadedClassCount());
        return builder.build().toString();
    }

    @GET
    @Path(PATH_COMPILATION)
    @Produces(APPLICATION_JSON)
    public String compilation() {
        final JsonObjectBuilder builder = createObjectBuilder();
        CompilationMXBean comp = ManagementFactory.getCompilationMXBean();
        builder.add("name", comp.getName());
        builder.add("totalCompilationTime", comp.getTotalCompilationTime());
        builder.add("compilationTimeMonitoringSupported", comp.isCompilationTimeMonitoringSupported());
        return builder.build().toString();
    }
}
