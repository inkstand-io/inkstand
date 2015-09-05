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

package io.inkstand.mgmt;

import static org.slf4j.LoggerFactory.getLogger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.inkstand.Management;
import io.inkstand.MicroServiceController;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.slf4j.Logger;

/**
 * Basic Management Servlet for Inkstand Microservice container that provides basic control functions such as starting
 * and stopping the inkstand container.<br> The servlet serves the following endpoints <ul>
 * <li>/mgtm/control/status/</li> <li>/mgtm/control/</li> </ul> Created by Gerald on 09.08.2015.
 */
@Management
@WebServlet(name = "control",
            description = "Inkstand Container Control Servlet",
            urlPatterns = { "/inkstand/control/*" })
public class ContainerControlServlet extends HttpServlet {

    /*
     * This servlet is intentionally not implemented using Jax-RS resources.
     * As Inkstand Microservices have a web container as minimal requirement, every instance of Inkstand
     * should be capable of running servlets including the management servlets, even without adding
     * a Jax-RS module.
     */

    private static final Logger LOG = getLogger(ContainerControlServlet.class);
    public static final String ATTR_JSON = "json";

    private ServletConfig config;

    @Inject
    private MicroServiceController msc;

    @Override
    public void init(final ServletConfig config) throws ServletException {

        LOG.info("Management Servlet {} initialized", this);
        this.config = config;

        //if the servlet was not instantiated in a CDI container it has to
        //be added to the current cdi context
        if (this.msc == null) {
            addToContext(this);
        }
    }

    /**
     * Adds this servlet to the cdi context and injecting all unresolved dependencies from the cdi context-
     *
     * @param unmanagedInstance
     *         the instance whose depenencies should be resolved using the context
     */
    private void addToContext(final Object unmanagedInstance) {
        //TODO potential candidate for CDI Utility
        final CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();
        final BeanManager beanManager = cdiContainer.getBeanManager();
        final CreationalContext creationalContext = beanManager.createCreationalContext(null);
        final AnnotatedType annotatedType = beanManager.createAnnotatedType(unmanagedInstance.getClass());
        final InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(unmanagedInstance, creationalContext);
    }

    @Override
    public ServletConfig getServletConfig() {

        return config;
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        LOG.debug("{} {}", req.getMethod(), req.getPathInfo());
        resp.setContentType("application/json");

        try (final JsonGenerator out = Json.createGenerator(resp.getOutputStream())) {
            if (isJsonAccepted(req)) {
                req.setAttribute("json", out);
                super.service(req, resp);
            } else {
                sendError(resp, out, 406, "ContentType: " + req.getHeader("Accept") + " not supported");
            }
        }
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        final JsonGenerator out = (JsonGenerator) req.getAttribute("json");

        final String resourcePath = req.getPathInfo();
        LOG.info("GET resource;{}", resourcePath);
        if ("/status/".equals(resourcePath)) {
            out.writeStartObject();
            out.write("state", msc.getState().toString());
            out.writeEnd();
        } else {
            sendError(resp, out, 404, "Resource not found", "resource", resourcePath);
        }

    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        final JsonGenerator out = (JsonGenerator) req.getAttribute(ATTR_JSON);
        final JsonObject json = readJsonBody(req);
        int delay = json.getInt("delay", 5);

        final String resourcePath = req.getPathInfo();
        if ("/shutdown/".equals(resourcePath)) {
            out.writeStartObject();
            out.write("msg", "ok");
            out.writeEnd();
            LOG.info("Shutting down {} in {}s", msc, delay);
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(new Runnable() {

                @Override
                public void run() {

                    LOG.info("Shutting down {}", msc);
                    //TODO decide which is the better option: shutdown CDI container or just the MS
                    //in any case, even the management port will become unavailable, so shutting down the
                    // microservice is only of advantage, when there are other options to control the container
                    msc.shutdown();
                    //CdiContainerLoader.getCdiContainer().shutdown();
                    LOG.info("Shutdown complete.");
                }
            }, delay, TimeUnit.SECONDS);

        } else {
            sendError(resp, out, 404, "Resource not found", "resource", resourcePath);
        }
    }

    private JsonObject readJsonBody(final HttpServletRequest req) throws IOException {

        final JsonReader reader = Json.createReader(req.getInputStream());
        return reader.readObject();
    }

    /**
     * Verifies if the request client accepts json as response
     *
     * @param request
     *         the http request containing the client information
     *
     * @return true if the client accepts application/json
     */

    private boolean isJsonAccepted(final HttpServletRequest request) {

        final String acceptHeader = request.getHeader("Accept");
        return acceptHeader == null || acceptHeader.contains("application/json");
    }

    /**
     * Creates an error response to be sent to the client. The response contains not only a status code but also a json
     * body with information for the client about the error.
     *
     * @param resp
     *         the servlet response
     * @param out
     *         the json generator for creating the response body
     * @param status
     *         the http status code to be returned
     * @param message
     *         the message to be returned
     * @param params
     *         parameters to be included in the response. There must name-value pairs.
     */
    private void sendError(final HttpServletResponse resp,
                           final JsonGenerator out,
                           final int status,
                           final String message,
                           final String... params) {

        resp.setStatus(status);
        out.writeStartObject();
        out.write("message", message);
        for (int i = 0; i < params.length; i += 2) {
            out.write(params[i], params[i + 1]);
        }
        out.writeEnd();
    }

    @Override
    public String getServletInfo() {

        return "Inkstand Management Servlet";
    }

    @Override
    public void destroy() {

    }
}
