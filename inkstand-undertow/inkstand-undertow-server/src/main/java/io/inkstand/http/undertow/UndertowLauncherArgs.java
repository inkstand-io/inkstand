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

import static io.inkstand.http.undertow.UndertowDefaultConfigurationProducer.HTTP_HOSTNAME_PROPERTY;
import static io.inkstand.http.undertow.UndertowDefaultConfigurationProducer.HTTP_PORT_PROPERTY;
import static io.inkstand.util.CollectionHelper.asUnmodifiableSet;

import java.util.Set;

import io.inkstand.LauncherArgs;

/**
 * Created by Gerald on 01.08.2015.
 */
public class UndertowLauncherArgs implements LauncherArgs {

    private static final Set<String> ARG_NAMES = asUnmodifiableSet("port", "hostname");
    /**
     * Command line argument to set the port of the http server
     */
    public static final String PORT = "port";
    /**
     * Command line argument to set the hostname or bind address of the http server
     */
    public static final String HOSTNAME = "hostname";

    @Override
    public Set<String> getArgNames() {
        return ARG_NAMES;
    }

    @Override
    public void apply(final String argName, final String value) {
        if(PORT.equals(argName)){
            applyPort(value);
        } else
        if(HOSTNAME.equals(argName)){
            applyHostname(value);
        } else {
            throw new IllegalArgumentException(argName + " is no valid argument");
        }
    }

    private void applyPort(final String value) {
        if(value == null) {
            throw new IllegalArgumentException("Port must not be null");
        }

        System.setProperty(HTTP_PORT_PROPERTY, value);
    }

    private void applyHostname(final String value) {
        if(value == null) {
            throw new IllegalArgumentException("Hostname must not be null");
        }
        System.setProperty(HTTP_HOSTNAME_PROPERTY, value);
    }

    @Override
    public String getDescription(final String argName) {
        if(PORT.equals(argName)){
            return "The TCP port the http server accepts incoming requests.";
        } else
        if(HOSTNAME.equals(argName)){
            return "The hostname of the http server";
        }
        throw new IllegalArgumentException(argName + " is no valid argument");
    }
}
