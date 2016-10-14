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

package io.inkstand.deployment.mgmt;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import io.inkstand.Management;
import io.inkstand.config.SimpleWebServerConfiguration;
import io.inkstand.config.WebServerConfiguration;
import org.apache.deltaspike.core.api.config.ConfigProperty;

/**
 * Management configuration producer for an {@link WebServerConfiguration}.
 * The values ar specified (via Delta Spike {@link ConfigProperty} injection), if none are specified, the default
 * values {@code localhost:7999}  will be used.
 * The property names are
 * <ul>
 * <li><code>inkstand.mgmt.port</code></li>
 * <li><code>inkstand.mgmt.listenaddress</code></li>
 * </ul>
 *
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class UndertowManagementConfigurationProducer {

    public static final String HTTP_PORT_PROPERTY = "inkstand.mgmt.port";
    public static final String HTTP_HOSTNAME_PROPERTY = "inkstand.mgmt.listenaddress";

    @Inject
    @ConfigProperty(name = HTTP_PORT_PROPERTY, defaultValue = "7999")
    private Integer port;

    @Inject
    @ConfigProperty(name = HTTP_HOSTNAME_PROPERTY, defaultValue = "localhost")
    private String bindAddress;

    @Produces
    @Management
    public WebServerConfiguration getConfiguration(){
        return new SimpleWebServerConfiguration(bindAddress, port);
    }

}
