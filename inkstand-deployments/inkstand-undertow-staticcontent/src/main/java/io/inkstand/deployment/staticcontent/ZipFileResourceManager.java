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

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import io.inkstand.InkstandRuntimeException;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;

/**
 * An Undertow {@link ResourceManager} that serves resources from a zip file.
 * Created by Gerald on 26.07.2015.
 */
public class ZipFileResourceManager implements ResourceManager {

    /**
     * The zip file containing the resource to be served by this manager.
     */
    private final ZipFile zipFile;

    /**
     * Creates the resource manager for serving the contents of the zip file.
     * @param zipFile
     */
    public ZipFileResourceManager(File zipFile) {

        try {
            this.zipFile = new ZipFile(zipFile);
        } catch (IOException e) {
            throw new InkstandRuntimeException("Could not read content from zip file  " + zipFile, e);
        }
    }

    @Override
    public Resource getResource(final String path) throws IOException {

        return new ZipFileResource(this.zipFile, path);
    }

    @Override
    public boolean isResourceChangeListenerSupported() {

        return false;
    }

    @Override
    public void registerResourceChangeListener(final ResourceChangeListener listener) {

    }

    @Override
    public void removeResourceChangeListener(final ResourceChangeListener listener) {

    }

    @Override
    public void close() throws IOException {
        this.zipFile.close();
    }
}
