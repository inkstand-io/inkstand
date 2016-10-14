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

package io.inkstand.config;

/**
 * Simple implementation of a {@link WebServerConfiguration} that has immutable values. Its intended to
 * be used by CDI producers.
 * Created by Gerald Muecke on 13.10.2015.
 */
public class SimpleWebServerConfiguration implements WebServerConfiguration{

    private final int port;
    private final String bindAdress;

    public SimpleWebServerConfiguration(final String bindAdress, final int port) {

        this.port = port;
        this.bindAdress = bindAdress;
    }

    public int getPort() {

        return port;
    }

    @Override
    public String getBindAddress() {

        return bindAdress;
    }

}
