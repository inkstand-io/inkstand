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
 * Basic Management Servlet for Inkstand Microservice container that provides basic control
 * functions such as starting and stopping the inkstand container.<br>
 * The servlet serves the following endpoints
 * <ul>
 *     <li>/mgtm/control/status/</li>
 *     <li>/mgtm/control/</li>
 * </ul>
 * Created by Gerald on 09.08.2015.
 */
@Management
@WebServlet(name="control",
            description = "Inkstand Container Control Servlet",
            urlPatterns={"/inkstand/control/*"})
public class ContainerControlServlet extends HttpServlet {

    /*
     * This servlet is intentionally not implemented using Jax-RS resources.
     * As Inkstand Microservices have a web container as minimal requirements, every instance of Inkstand
     * should be capable of running servlets.
     */

    private static final Logger LOG = getLogger(ContainerControlServlet.class);

    private ServletConfig config;

    @Inject
    private MicroServiceController msc;


    @Override
    public void init(final ServletConfig config) throws ServletException {

        LOG.info("Management Servlet {} initialized", this);
        this.config = config;

        //if the servlet was not instantiated in a CDI container it has to
        //be added to the current cdi context
        if(this.msc == null) {

            addToContext(this);

        }

    }

    /**
     * Adds this servlet to the cdi context and injecting all unresolved dependencies from the cdi context-
     * @param unmanagedInstance
     *  the instance whose depenencies should be resolved using the context
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
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        try(final JsonGenerator out = Json.createGenerator(resp.getOutputStream())) {
            if(isJsonAccepted(req)) {

                String resourcePath = req.getPathInfo();
                LOG.info("GET resource;{}", resourcePath);
                if("/status/".equals(resourcePath)) {
                    out.writeStartObject();
                    out.write("state", msc.getState().toString());
                    out.writeEnd();
                } else {
                    resp.setStatus(404);
                    out.writeStartObject();
                    out.write("message", "Resource not found");
                    out.write("resource", resourcePath);
                    out.writeEnd();
                }
            } else {
                resp.setStatus(406);
                out.writeStartObject();
                out.write("message", "ContentType: " + req.getContentType() + " not supported");
                out.writeEnd();
            }
        }
    }

    private boolean isJsonAccepted(final HttpServletRequest request) {
        final String acceptHeader = request.getHeader("Accept");
        return acceptHeader == null ||acceptHeader.contains("application/json");
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        final JsonReader reader = Json.createReader(req.getInputStream());
        JsonObject json = reader.readObject();
        int delay = json.getInt("delay", 5);

        final JsonGenerator out = Json.createGenerator(resp.getOutputStream());
        out.writeStartObject();
        out.write("msg", "ok");
        out.writeEnd();
        out.close();

        LOG.info("Shutting down {} in {}s", msc, delay);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(new Runnable() {

            @Override
            public void run() {

                LOG.info("Shutting down {}", msc);
                //msc.stop();
                CdiContainerLoader.getCdiContainer().shutdown();
                LOG.info("Shutdown complete.");
            }
        }, delay, TimeUnit.SECONDS);

    }



    @Override
    public String getServletInfo() {

        return "Inkstand Management Servlet";
    }

    @Override
    public void destroy() {

    }
}
