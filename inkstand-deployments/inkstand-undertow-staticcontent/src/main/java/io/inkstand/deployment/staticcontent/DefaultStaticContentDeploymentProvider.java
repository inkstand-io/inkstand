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

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.File;
import org.apache.deltaspike.core.api.config.ConfigProperty;

import io.inkstand.http.undertow.UndertowDeploymentProvider;
import io.undertow.servlet.api.DeploymentInfo;

/**
 * Created by Gerald on 26.07.2015.
 */
public class DefaultStaticContentDeploymentProvider implements UndertowDeploymentProvider {

    @Inject
    @ConfigProperty(name = "inkstand.http.content.zip")
    private String contentFileLocation; //NOSONAR

    @Override
    @Produces
    public DeploymentInfo getDeployment() {

        final File contentFile = new File(contentFileLocation); //NOSONAR

        return new DeploymentInfo()
        .setContextPath("/")
        .setResourceManager(new ZipFileResourceManager(contentFile))
        .setDeploymentName("StaticContent")
        .setClassLoader(ClassLoader.getSystemClassLoader());

    }
}
