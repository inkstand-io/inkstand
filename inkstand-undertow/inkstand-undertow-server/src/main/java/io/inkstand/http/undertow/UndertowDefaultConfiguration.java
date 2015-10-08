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

package io.inkstand.http.undertow;

import javax.inject.Inject;
import org.apache.deltaspike.core.api.config.ConfigProperty;

import io.inkstand.config.WebServerConfiguration;
import io.undertow.Undertow;

/**
 * Default configuration for an {@link Undertow} web container that defines the listen address and the port. If none is
 * specified (via Delta Spike {@link ConfigProperty} injection), the default values localhost:80 will be used. The
 * property names are
 * <ul>
 * <li><code>inkstand.http.port</code></li>
 * <li><code>inkstand.http.listenaddress</code></li>
 * </ul>
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class UndertowDefaultConfiguration implements WebServerConfiguration {

    public static final String HTTP_PORT_PROPERTY = "inkstand.http.port";
    public static final String HTTP_HOSTNAME_PROPERTY = "inkstand.http.listenaddress";

    @Inject
    @ConfigProperty(name = HTTP_PORT_PROPERTY, defaultValue = "80")
    private Integer port;

    @Inject
    @ConfigProperty(name = HTTP_HOSTNAME_PROPERTY, defaultValue = "localhost")
    private String bindAddress;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getBindAddress() {
        return bindAddress;
    }

}
