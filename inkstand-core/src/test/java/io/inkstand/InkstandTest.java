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

import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import org.jboss.weld.environment.se.StartMain;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Gerald on 31.07.2015.
 */
public class InkstandTest {

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
        StartMain.PARAMETERS = null;
        baos.reset();
    }

    @After
    public void tearDown() throws Exception {
        StartMain.PARAMETERS = null;
        TestLauncherArgs.reset();
    }

    /**
     * The test verifies that the TestLauncherArgs is not called to apply any argument (as there is none) and
     * that the Weld CDI container ist started using the {@link StartMain} .
     * @throws Exception
     */
    @Test
    public void testMain_noArgs() throws Exception {

        //prepare
        assumeThat(StartMain.PARAMETERS, nullValue());
        String[] args = new String[0];

        //act
        Inkstand.main(args);

        //assert
        assertArrayEquals(args, StartMain.getParameters());
        assertFalse(TestLauncherArgs.isApplyInvoked());

    }

    /**
     * The test verifies that the ? arg is applied to the HelpLauncherArgs which will print out all descriptions
     * of all LauncherArgs, including the TestLauncherArgs.
     * @throws Exception
     */
    @Test
    public void testMain_helpArgs() throws Exception {

        assumeTrue(!"pit".equals(System.getProperty("tests.mode")));
        assumeThat(StartMain.PARAMETERS, nullValue());
        //prepare
        //pass the help arg
        String[] args = new String[]{"-?"};

        //act
        Inkstand.main(args);

        //assert
        assertArrayEquals(args, StartMain.getParameters());

        //the help launcher arg should display the description of all launcherArg extensions on system out
        //so the output should contain the description of the TestLauncherArgs extension
        System.setOut(originalOutStream);
        final String consoleOut = new String(baos.toByteArray());
        System.out.println(consoleOut);
        assertTrue(consoleOut.contains("testDescription"));


    }

    /**
     * The test verifies that the TestLauncherArgs is not called to apply any argument (as there is none) and
     * that the Weld CDI container ist started using the {@link StartMain} .
     * @throws Exception
     */
    @Test
    public void testMain_applyArgs() throws Exception {
        assumeThat(StartMain.PARAMETERS, nullValue());

        //prepare
        String[] args = new String[]{"-test", "testvalue"};

        //act
        Inkstand.main(args);

        //assert
        assertArrayEquals(args, StartMain.getParameters());
        assertTrue(TestLauncherArgs.isApplyInvoked());
        final Map<String,String> appliedArgs = TestLauncherArgs.getAppliedArgs();
        assertTrue(appliedArgs.containsKey("test"));
        assertEquals("testvalue", appliedArgs.get("test"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMain_applyArgs_invalidArgs_exception() throws Exception {
        assumeThat(StartMain.PARAMETERS, nullValue());

        //prepare
        String[] args = new String[]{"-unknown", "-test", "testvalue"};

        //act
        Inkstand.main(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMain_applyArgs_invalidValue_exception() throws Exception {
        assumeThat(StartMain.PARAMETERS, nullValue());

        //prepare
        String[] args = new String[]{"unknown", "testvalue"};

        //act
        Inkstand.main(args);
    }


    /**
     * The test verifies that the TestLauncherArgs is called to apply a flag argument (without value) and
     * that the Weld CDI container ist started using the {@link StartMain}.
     * @throws Exception
     */
    @Test
    public void testMain_applyArgs_flagArg() throws Exception {
        assumeThat(StartMain.PARAMETERS, nullValue());

        //prepare
        String[] args = new String[]{"-test", "-other"};

        //act
        Inkstand.main(args);

        //assert
        assertArrayEquals(args, StartMain.getParameters());
        assertTrue(TestLauncherArgs.isApplyInvoked());
        final Map<String,String> appliedArgs = TestLauncherArgs.getAppliedArgs();
        assertTrue(appliedArgs.containsKey("test"));
        assertTrue(appliedArgs.containsKey("other"));
        assertEquals(null, appliedArgs.get("test"));
        assertEquals(null, appliedArgs.get("other"));

    }

}
