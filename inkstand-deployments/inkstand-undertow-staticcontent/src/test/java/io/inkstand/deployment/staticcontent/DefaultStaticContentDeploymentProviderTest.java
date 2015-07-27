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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import io.inkstand.scribble.Scribble;
import io.inkstand.scribble.rules.TemporaryFile;
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
    public void testGetDeployment() throws Exception {

        //prepare
        inject(file.getFile().getAbsolutePath()).asConfigProperty("inkstand.http.content.zip").into(subject);

        //act
        DeploymentInfo di = this.subject.getDeployment();

        //assert
        assertNotNull(di);
        assertEquals("/", di.getContextPath());
        assertEquals("StaticContent", di.getDeploymentName());
    }
}
