package io.inkstand.mgmt;

import static org.slf4j.LoggerFactory.getLogger;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import io.inkstand.Management;
import org.slf4j.Logger;

/**
 * Created by Gerald Mücke on 31.08.2015.
 */
@Management
@WebServlet(name="jmx",
            description = "Inkstand JMX Servlet",
            urlPatterns={"/inkstand/jmx/*"})
public class JmxServlet extends HttpServlet {

    private static final Logger LOG = getLogger(JmxServlet.class);

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        try(final JsonGenerator out = Json.createGenerator(resp.getOutputStream())) {
            if (isJsonAccepted(req)) {

                String resourcePath = req.getPathInfo();
                LOG.info("GET resource {}", resourcePath);
                if ("/".equals(resourcePath)) {
                    out.writeStartObject();
                    out.writeStartObject("_links");
                    out.write("runtime", req.getPathInfo() + "/runtime");
                    out.write("memory", req.getPathInfo() + "/memory");
                    out.write("os", req.getPathInfo() + "/os");
                    out.write("threads", req.getPathInfo() + "/threads");
                    out.write("classloading", req.getPathInfo() + "/classloading");
                    out.write("compilation", req.getPathInfo() + "/compilation");
                    out.writeEnd();
                    out.writeEnd();
                } else {
                    resp.setStatus(404);
                    out.writeStartObject();
                    out.write("message", "Resource not found");
                    out.write("resource", resourcePath);
                    out.writeEnd();
                }
            }
        }
    }

    private boolean isJsonAccepted(final HttpServletRequest request) {
        final String acceptHeader = request.getHeader("Accept");
        return acceptHeader == null ||acceptHeader.contains("application/json");
    }
}
