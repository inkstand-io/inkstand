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

package io.inkstand;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.util.concurrent.atomic.AtomicReference;

import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Launcher for an injectable microservice.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 */
@ApplicationScoped
public class MicroServiceController implements MicroService.StateSupport{

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(MicroServiceController.class);

    @Inject
    private MicroService microService;

    private final AtomicReference<State> trackedState = new AtomicReference<>(State.NEW);

    @PostConstruct
    public void init() {
        LOG.info("Starting '{}'", microService);
        microService.start();
        trackedState.set(State.RUNNING);
    }

    void watch(@Observes final ContainerInitialized containerInitialized) {
        LOG.debug("Container initialized");
        LOG.info("Inkstand '{}' running", microService);
    }

    @PreDestroy
    public void shutdown() {
        microService.stop();
        trackedState.set(State.STOPPED);
        LOG.info("'{}' stopped", microService);
    }

    @Override
    public State getState(){
        if(microService instanceof MicroService.StateSupport){
            return ((MicroService.StateSupport) microService).getState();
        }
        return trackedState.get();
    }
}
