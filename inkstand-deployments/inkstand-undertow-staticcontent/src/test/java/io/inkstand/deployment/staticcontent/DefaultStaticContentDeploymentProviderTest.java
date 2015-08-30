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

import static io.inkstand.scribble.Scribble.inject;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.rules.TemporaryFile;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.api.DeploymentInfo;

/**
 * Created by Gerald on 27.07.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultStaticContentDeploymentProviderTest {

    /**
     * The class under test
     */
    @InjectMocks
    private DefaultStaticContentDeploymentProvider subject;

    @Rule
    public TemporaryFile file = Scribble.newTempFolder().aroundTempFile("testfile.zip").withContent()
                                        .fromClasspathResource("/io/inkstand/deployment/staticcontent/content.zip")
                                        .build();


    @Test
    public void testGetDeployment_zipContentFile() throws Exception {

        //prepare
        Scribble.inject(file.getFile().getAbsolutePath()).asConfigProperty("inkstand.http.content.root").into(subject);
        Scribble.inject(null).asConfigProperty("inkstand.http.indexFile").into(subject);

        //act
        DeploymentInfo di = this.subject.getDeployment();

        //assert
        assertNotNull(di);
        assertEquals("/", di.getContextPath());
        assertEquals("[index.html]", di.getWelcomePages().toString());
        assertEquals("StaticContent", di.getDeploymentName());
        ResourceManager rm = di.getResourceManager();
        //the index.html file is contained in the testfile.zip
        assertNotNull(rm.getResource("index1.html"));
        assertNull(rm.getResource("/testfile.zip"));
    }

    @Test
    public void testGetDeployment_fsContentRoot() throws Exception {

        //prepare
        Scribble.inject(file.getFile().getParentFile().getAbsolutePath()).asConfigProperty("inkstand.http.content.root").into(
                subject);
        Scribble.inject(null).asConfigProperty("inkstand.http.indexFile").into(subject);

        //act
        DeploymentInfo di = this.subject.getDeployment();

        //assert
        assertNotNull(di);
        assertEquals("/", di.getContextPath());
        assertEquals("[index.html]", di.getWelcomePages().toString());
        assertEquals("StaticContent", di.getDeploymentName());
        //the resource manager should serve content in the DIRECTORY of the content.zip
        //therefore there is no index.html resource (it's in the testfile.zip) but a resource named "testfile.zip"
        ResourceManager rm = di.getResourceManager();
        assertNull(rm.getResource("index1.html"));
        assertNotNull(rm.getResource("/testfile.zip"));
    }
}
