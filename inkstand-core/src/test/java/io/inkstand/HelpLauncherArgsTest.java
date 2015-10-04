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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Gerald on 01.08.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class HelpLauncherArgsTest {

    /**
     * The class under test
     */
    @InjectMocks
    private HelpLauncherArgs subject;

    private static PrintStream originalOutStream;
    private static ByteArrayOutputStream baos;

    @BeforeClass
    public static void setUpSystemOut() throws Exception {
        originalOutStream = System.out;
        //prepare the sysout stream to record the console output
        //in order to record the system out, the output for the console has to be redirected. The console output
        //is written using an SLF4J logger, see log4j2.xml config for the console output configuration
        baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
    }

    @AfterClass
    public static void tearDownSystemOut() throws Exception {
        System.setOut(originalOutStream);
    }

    @Before
    public void setUp() throws Exception {
        baos.reset();
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testGetArgNames() throws Exception {
        //prepare

        //act
        Set<String> argNames = subject.getArgNames();

        //assert
        assertNotNull(argNames);
        assertEquals(1, argNames.size());
        assertTrue(argNames.contains("?"));
    }

    @Test
    public void testApply() throws Exception {

        //prepare

        //act
        subject.apply("?", null);

        //assert
        //the help launcher arg should display the description of all launcherArg extensions on system out
        //so the output should contain the description of the TestLauncherArgs extension
        System.setOut(originalOutStream);
        final String consoleOut = new String(baos.toByteArray());
        System.out.println(consoleOut);
        assertTrue(consoleOut.contains("testDescription"));
    }

    @Test
    public void testGetDescription() throws Exception {

        //prepare

        //act
        String desc = subject.getDescription("?");


        //assert
        assertEquals("Displays a list of all available command line arguments.", desc);
    }
}
