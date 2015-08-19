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

import static org.slf4j.LoggerFactory.getLogger;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.File;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.slf4j.Logger;

import io.inkstand.http.undertow.UndertowDeploymentProvider;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.api.DeploymentInfo;

/**
 * Created by Gerald on 26.07.2015.
 */
public class DefaultStaticContentDeploymentProvider implements UndertowDeploymentProvider {

    private static final Logger LOG = getLogger(DefaultStaticContentDeploymentProvider.class);
    /**
     * The configuration property for the location of the static content. Could point to a zip file or a directory.
     */
    public static final String HTTP_CONTENT_ROOT_PROPERTY = "inkstand.http.content.root";

    @Inject
    @ConfigProperty(name = HTTP_CONTENT_ROOT_PROPERTY)
    private String contentFileLocation; //NOSONAR

    @Inject
    @ConfigProperty(name = "inkstand.http.indexFile",  defaultValue= "index.html")
    private String indexFile;

    @Override
    @Produces
    public DeploymentInfo getDeployment() {

        final ResourceManager resMgr = createResourceManager();

        return new DeploymentInfo()
        .setContextPath(indexFile)
        .setResourceManager(resMgr)
        .setDeploymentName("StaticContent")
        .setClassLoader(ClassLoader.getSystemClassLoader());
    }

    private ResourceManager createResourceManager() {
        LOG.info("Serving content from {}", contentFileLocation);
        final File contentFile = new File(contentFileLocation); //NOSONAR

        if(contentFile.getName().endsWith(".zip")){
            return new ZipFileResourceManager(contentFile);
        } else {
            //data chunk for responding is set to 64K bytes
            return new FileResourceManager(contentFile, 65_536L);
        }
    }
}
