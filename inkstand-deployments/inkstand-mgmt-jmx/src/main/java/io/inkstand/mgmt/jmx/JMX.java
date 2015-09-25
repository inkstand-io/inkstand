package io.inkstand.mgmt.jmx;

import static javax.json.Json.createObjectBuilder;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

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

        return builder.build().toString();
    }

    @GET
    @Path("/memory")
    public JsonObject memory() {

        return null;
    }

    @GET
    @Path("/os")
    public JsonObject os() {

        return null;
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
