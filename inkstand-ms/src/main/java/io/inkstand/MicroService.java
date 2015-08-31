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

package io.inkstand;

/**
 * Interface to for a web server. Inkstand is extensible to support various embeddable web servers.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 * 
 */
public interface MicroService {

    /**
     * Starts the web server
     */
    void start();

    /**
     * Stops the web server
     */
    void stop();

    /**
     * Extension interface for {@link MicroService}. MicroService implementations may implement this interface
     * to provide information about the current state of the service.
     * Created by Gerald Mücke on 30.08.2015.
     */
    interface StateSupport {

        /**
         * Provides the information of the current state of the service.
         * @return
         *  the current state of the implementor
         */
        State getState();

        /**
         *
         * The state of the microservice. The basic state machine for a microservice is
         * <ul>
         *     <li>{@code (create)}: -&gt; NEW</li>
         *     <li>{@code start()}: NEW -&gt; RUNNING</li>
         *     <li>{@code stop()}: RUNNING -&gt; STOPPED</li>
         *     <li>{@code start()}: STOPPED -&gt; RUNNING</li>
         * </ul>
         * @return
         *  the current state of the microservice.
         *
         */
        enum State {
            NEW,
            RUNNING,
            STOPPED,
            ;
        }


    }

}
