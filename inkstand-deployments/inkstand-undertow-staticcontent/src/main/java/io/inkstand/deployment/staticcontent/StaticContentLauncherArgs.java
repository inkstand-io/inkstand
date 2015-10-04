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
import static io.inkstand.util.CollectionHelper.asUnmodifiableSet;

import java.util.Set;

import io.inkstand.LauncherArgs;

/**
 * Created by Gerald on 01.08.2015.
 */
public class StaticContentLauncherArgs implements LauncherArgs {

    /**
     * The content root containing the static content to be served. The value may be a path to a directory or
     * to a zip file containing the content.
     */
    public static final String CONTENT_ROOT = "contentRoot";

    private static final Set<String> ARG_NAMES = asUnmodifiableSet("contentRoot");

    @Override
    public Set<String> getArgNames() {

        return ARG_NAMES;
    }

    @Override
    public void apply(final String argName, final String value) {

        if(CONTENT_ROOT.equals(argName)){
            applyContentRoot(value);
        } else {
            throw new IllegalArgumentException(argName + " is no valid argument");
        }

    }

    private void applyContentRoot(final String contentRoot) {
        if(contentRoot == null) {
            throw new IllegalArgumentException("Value for contentRoot is missing");
        }
        System.setProperty(HTTP_CONTENT_ROOT_PROPERTY, contentRoot);

    }

    @Override
    public String getDescription(final String argName) {

        if(CONTENT_ROOT.equals(argName)){
            return "The path to the directory or zip file containing the static content.";
        }
        throw new IllegalArgumentException(argName + " is no valid argument");
    }
}
