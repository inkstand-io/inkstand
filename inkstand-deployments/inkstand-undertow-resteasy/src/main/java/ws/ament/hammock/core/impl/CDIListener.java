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

package ws.ament.hammock.core.impl;

import org.jboss.weld.context.bound.BoundRequestContext;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.HashMap;
import java.util.Map;

/**
 * A servlet request listener that starts a request context within the scope of a thread.
 *
 * A simplified format of the weld servlet approach, withouth dealing with all of the things needed for full blown app requirements.
 *
 * Based on DeltaSpike's Container Control paradigm.
 *
 */
public class CDIListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        BoundRequestContext requestContext = (BoundRequestContext)servletRequestEvent.getServletRequest().getAttribute("cdiRequestContext");
        @SuppressWarnings("unchecked")
        Map<String,Object> requestMap = (Map<String,Object>)servletRequestEvent.getServletRequest().getAttribute("cdiRequestMap");
        requestContext.invalidate();
        requestContext.deactivate();
        requestContext.dissociate(requestMap);
        servletRequestEvent.getServletRequest().setAttribute("cdiRequestContext",null);
        servletRequestEvent.getServletRequest().setAttribute("cdiRequestMap",null);
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        BoundRequestContext requestContext = CDI.current().select(BoundRequestContext.class).get();
        Map<String,Object> requestMap = new HashMap<>();
        requestContext.associate(requestMap);
        requestContext.activate();
        servletRequestEvent.getServletRequest().setAttribute("cdiRequestContext",requestContext);
        servletRequestEvent.getServletRequest().setAttribute("cdiRequestMap",requestMap);
    }
}