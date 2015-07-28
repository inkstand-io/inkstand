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

package io.inkstand.deployment.staticcontent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.rules.TemporaryFile;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;

/**
 * Created by Gerald on 26.07.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ZipFileResourceManagerTest {

    /**
     * The class under test
     */
    private ZipFileResourceManager subject;

    //@formatter:off
    @Rule
    public final TemporaryFile file = Scribble.newTempFolder().aroundTempFile("content.zip")
                                          .withContent()
                                          .fromClasspathResource("/io/inkstand/deployment/staticcontent/content.zip")
                                          .build();
    //@formatter:on

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        subject = new ZipFileResourceManager(file.getFile());

    }

    @Test
    public void testGetResource_nonLeadingSlashPath_nonLeadingSlashEntry_success() throws Exception {

        //prepare

        //act
        Resource resource = subject.getResource("index1.html");

        //assert
        assertNotNull(resource);
        assertEquals("index1.html", resource.getPath());
    }

    @Test
    public void testGetResource_leadingSlashPath_nonLeadingSlashEntry_success() throws Exception {

        //prepare

        //act
        Resource resource = subject.getResource("/index1.html");

        //assert
        assertNotNull(resource);
        assertEquals("/index1.html", resource.getPath());
    }

    @Test
    public void testGetResource_nonLeadingSlashPath_leadingSlashEntry_success() throws Exception {

        //prepare

        //act
        Resource resource = subject.getResource("index2.html");

        //assert
        assertNotNull(resource);
        assertEquals("index2.html", resource.getPath());
    }

    @Test
    public void testGetResource_leadingSlashPath_leadingSlashEntry_success() throws Exception {

        //prepare

        //act
        Resource resource = subject.getResource("/index2.html");

        //assert
        assertNotNull(resource);
        assertEquals("/index2.html", resource.getPath());
    }

    @Test
    public void testIsResourceChangeListenerSupported() throws Exception {

        assertFalse(subject.isResourceChangeListenerSupported());
    }

    @Test
    public void testClose() throws Exception {

        //prepare
        exception.expect(IllegalStateException.class);
        exception.expectMessage("zip file closed");

        //act
        subject.close();

        //assert
        subject.getResource("/index.html");

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRegisterResourceChangeListener() throws Exception {
        //prepare
        ResourceChangeListener listener = mock(ResourceChangeListener.class);

        //act
        subject.registerResourceChangeListener(listener);

        //assert

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveResourceChangeListener() throws Exception {
        //prepare
        ResourceChangeListener listener = mock(ResourceChangeListener.class);

        //act
        subject.removeResourceChangeListener(listener);

        //assert

    }
}
