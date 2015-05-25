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

package io.inkstand.config;

import java.util.Collection;

/**
 * Configuration for the Application that should be run
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 * 
 */
public interface ApplicationConfiguration {

    /**
     * Context root
     * 
     * @return
     *  the context root the application runs in. Could be empty.
     */
    String getContextRoot();

    /**
     * The list of provider classes to register.
     * 
     * @return
     *  the Classes that denote a Jax-RS {@link javax.ws.rs.ext.Provider}
     */
    @SuppressWarnings("rawtypes")
    Collection<Class> getProviderClasses();

    /**
     * The list of resource classes to load.
     * 
     * @return
     *   the Classes that denote a Jax-RS {@link javax.ws.rs.Path} resource
     */
    @SuppressWarnings("rawtypes")
    Collection<Class> getResourceClasses();
}
