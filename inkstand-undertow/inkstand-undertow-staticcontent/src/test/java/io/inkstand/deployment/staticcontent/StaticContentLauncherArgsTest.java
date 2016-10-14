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

package io.inkstand.deployment.staticcontent;

import static io.inkstand.deployment.staticcontent.DefaultStaticContentDeploymentProvider.HTTP_CONTENT_ROOT_PROPERTY;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.util.Properties;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Gerald on 01.08.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class StaticContentLauncherArgsTest {

    /**
     * The class under test
     */
    @InjectMocks
    private StaticContentLauncherArgs subject;

    private Properties originalProperties;

    @Before
    public void setUp() throws Exception {

        originalProperties = System.getProperties();

    }

    @After
    public void tearDown() throws Exception {

        System.setProperties(originalProperties);
    }

    @Test
    public void testGetArgNames() throws Exception {
        //prepare

        //act
        Set<String> argNames = subject.getArgNames();

        //assert
        assertNotNull(argNames);
        assertEquals(1, argNames.size());
        assertTrue(argNames.contains("contentRoot"));
    }

    @Test
    public void testApply_contentRoot() throws Exception {

        //prepare
        assumeThat(System.getProperty(HTTP_CONTENT_ROOT_PROPERTY), nullValue());

        //act
        subject.apply("contentRoot", "/");

        //assert
        assertEquals("/", System.getProperty(HTTP_CONTENT_ROOT_PROPERTY));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApply_contentRoot_noValue() throws Exception {

        //act
        subject.apply("contentRoot", null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testApply_unknownArg() throws Exception {

        //prepare

        //act
        subject.apply("unknown", "111");

    }

    @Test
    public void testGetDescription_contentRoot() throws Exception {

        //prepare

        //act
        String desc = subject.getDescription("contentRoot");

        //assert
        assertEquals("The path to the directory or zip file containing the static content.", desc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDescription_unknownArg() throws Exception {

        //prepare

        //act
        subject.getDescription("unknown");

    }
}
