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

package io.inkstand.it;

import javax.enterprise.inject.Produces;

import io.inkstand.Management;
import io.inkstand.config.WebServerConfiguration;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;

/**
 * Created by Gerald on 02.08.2015.
 */
public class ManagamentITCaseConfig {

    private static int port = Integer.valueOf(System.getProperty("inkstand.mgmt.port"));


    @Produces
    @Management
    public DeploymentInfo getDeployment() {

        DeploymentInfo di = new DeploymentInfo();
        ServletInfo si = new ServletInfo("mgmtServler", ManagementServlet.class);
        si.addMapping("/*");
        di.addServlet(si);
        di.setDeploymentName("ManagementServlet");
        di.setContextPath("/mgmt");
        di.setClassLoader(ClassLoader.getSystemClassLoader());
        return di;
    }

    @Produces
    @Management
    public WebServerConfiguration getWebServerConfig() {

        return new WebServerConfiguration() {

            @Override
            public int getPort() {

                return port;
            }

            @Override
            public String getBindAddress() {

                return "localhost";
            }
        };

    }
}

