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

package io.inkstand.http.undertow;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicReference;

import io.inkstand.MicroService;
import io.undertow.Undertow;

/**
 * Undertow based WebServer implementation
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
public class UndertowWebServer implements MicroService, MicroService.StateSupport{

    @Inject
    private Undertow undertow;

    /**
     * The current state
     */
    private AtomicReference<State> state = new AtomicReference<>(State.NEW);

    @Override
    public void start() {
        undertow.start();
        state.set(State.RUNNING);
    }

    @Override
    public void stop() {
        undertow.stop();
        state.set(State.STOPPED);
    }

    @Override
    public State getState() {
        return this.state.get();
    }

    /**
     * @return the {@link Undertow} server instance.
     */
    public Undertow getUndertow() {
        return undertow;
    }

    @Override
    public String toString() {
        return "[Undertow]";
    }

}
