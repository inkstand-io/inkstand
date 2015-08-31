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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.inkstand.MicroService.StateSupport;
import io.inkstand.scribble.Scribble;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Service Control<br>
 *  The service itself is managed by the controller. Although possible, the {@link MicroService} should not
 *  be accessed directly but through the {@link MicroServiceController}. The controller is bound to the CDI
 *  container lifecycle and defines, when to start and stop the service.
 * State handling:<br>
 *  If the controlled microservice does provide {@link StateSupport}, the controller tracks the state.
 */
@RunWith(MockitoJUnitRunner.class)
public class MicroServiceControllerTest {

    @Mock
    private MicroService ms;

    @Mock(extraInterfaces = MicroService.class)
    private StateSupport statefulMs;

    @InjectMocks
    private MicroServiceController subject;

    @Before
    public void setUp() throws Exception {
        Scribble.inject(ms).asQualifyingInstance().into(subject);
    }

    @Test
    public void testInit() throws Exception {
        this.subject.init();
        verify(ms).start();
    }

    @Test
    public void testWatch() throws Exception {
        this.subject.watch(mock(ContainerInitialized.class));
        // nothing happens here, watch is just for the Standalone Weld Launcher
    }

    @Test
    public void testShutdown() throws Exception {
        this.subject.shutdown();
        verify(ms).stop();
    }

    @Test
    public void testGetState_noStateSupport_beforeInit_NEW() throws Exception {
        //prepare

        //act
        StateSupport.State state = subject.getState();

        //assert
        assertEquals(StateSupport.State.NEW, state);

    }
    @Test
    public void testGetState_noStateSupport_afterInit_RUNNING() throws Exception {
        //prepare
        subject.init();

        //act
        StateSupport.State state = subject.getState();

        //assert
        assertEquals(StateSupport.State.RUNNING, state);
    }

    @Test
    public void testGetState_noStateSupport_shutdowInit_STOPPED() throws Exception {
        //prepare
        subject.init();
        subject.shutdown();

        //act
        StateSupport.State state = subject.getState();

        //assert
        assertEquals(StateSupport.State.STOPPED, state);
    }

    @Test
    public void testGetState_stateSupport_stateFromService() throws Exception {
        //prepare
        Scribble.inject(statefulMs).asQualifyingInstance().into(subject);
        when(statefulMs.getState()).thenReturn(StateSupport.State.STOPPED);

        //act
        when(statefulMs.getState()).thenReturn(StateSupport.State.STOPPED);
        subject.init();
        assertEquals(StateSupport.State.STOPPED, subject.getState());

        when(statefulMs.getState()).thenReturn(StateSupport.State.RUNNING);
        subject.shutdown();
        assertEquals(StateSupport.State.RUNNING, subject.getState());

    }

}
