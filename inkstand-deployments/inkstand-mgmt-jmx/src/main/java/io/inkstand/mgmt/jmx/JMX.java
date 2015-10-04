package io.inkstand.mgmt.jmx;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

import io.inkstand.Management;

/**
 * Created by Gerald Mücke on 22.09.2015.
 */
@Management
@Path("/jmx")
public class JMX {

    @GET
    @Produces("application/json")
    public String introspection() {

        return createObjectBuilder().add("_links",
                                         createObjectBuilder().add("runtime", "/runtime")
                                                              .add("memory", "/memory")
                                                              .add("os", "/os")
                                                              .add("threads", "/threads")
                                                              .add("classloading", "/classloading")
                                                              .add("compilation", "/compilation")).build().toString();
    }

    @GET
    @Path("/runtime")
    @Produces("application/json")
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

        JsonArrayBuilder inputArgs = createArrayBuilder();
        for(String inputArg : runtime.getInputArguments()) {
            inputArgs.add(inputArg);
        }
        return inputArgs;
    }

    private JsonObjectBuilder getSystemProperties(final RuntimeMXBean runtime) {

        JsonObjectBuilder sysProps = createObjectBuilder();
        for(Map.Entry<String, String> sysprop : runtime.getSystemProperties().entrySet()){
            sysProps.add(sysprop.getKey(), sysprop.getValue());
        }
        return sysProps;
    }

    @GET
    @Produces("application/json")
    @Path("/memory")
    public String memory() {
        final JsonObjectBuilder builder = createObjectBuilder();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
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
    @Path("/os")
    public String os() {
        final JsonObjectBuilder builder = createObjectBuilder();
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        builder.add("arch", os.getArch());
        builder.add("name", os.getName());
        builder.add("version", os.getVersion());
        builder.add("processors", os.getAvailableProcessors());
        builder.add("sysLoadAvg", os.getSystemLoadAverage());
        return builder.build().toString();
    }

    @GET
    @Path("/threads")
    public JsonObject threads() {

        return null;
    }

    @GET
    @Path("/classloading")
    public JsonObject classloading() {

        return null;
    }

    @GET
    @Path("/compilation")
    public JsonObject compilation() {

        return null;
    }
}
