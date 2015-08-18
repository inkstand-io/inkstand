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

import static org.slf4j.LoggerFactory.getLogger;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import org.slf4j.Logger;

/**
 * Created by Gerald on 09.08.2015.
 */
public class ManagementServlet implements Servlet {

    private static final Logger LOG = getLogger(ManagementServlet.class);

    @Override
    public void init(final ServletConfig config) throws ServletException {

        LOG.info("Management Servlet initialized");
    }

    @Override
    public ServletConfig getServletConfig() {

        return null;
    }

    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {

        res.setContentType("application/json");
        final JsonGenerator out = Json.createGenerator(res.getOutputStream());
        out.writeStartObject();
        out.write("msg", "ok");
        out.writeEnd();
        out.close();
    }

    @Override
    public String getServletInfo() {

        return null;
    }

    @Override
    public void destroy() {

    }
}
