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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;

import io.inkstand.InkstandRuntimeException;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;

/**
 * An Undertow {@link ResourceManager} that serves resources from a zip file. All content may be provided in a single
 * zip file which makes bulk deployment and replacement much easier. Further, it's inherently not possible to
 * leave the content structure with relative paths.
 * Created by Gerald on 26.07.2015.
 */
public class ZipFileResourceManager implements ResourceManager {

    private static final Logger LOG = getLogger(ZipFileResourceManager.class);

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
            if(LOG.isDebugEnabled()){
                LOG.debug("Registered resources");
                final Enumeration<? extends ZipEntry> entries = this.zipFile.entries();
                while(entries.hasMoreElements()){
                    LOG.debug("{}", entries.nextElement().getName());
                }
            }
        } catch (IOException e) {
            throw new InkstandRuntimeException("Could not read content from zip file  " + zipFile, e);
        }
    }

    @Override
    public Resource getResource(final String path) throws IOException {

        //entries in the zip file can be with leading / or without
        ZipEntry entry;
        if(path.startsWith("/")){
            entry = getEntry(path, path.substring(1));
        } else {
            entry = getEntry("/" + path, path);
        }
        if(entry == null) {
            return null;
        }
        return new ZipFileResource(this.zipFile, entry, path);
    }

    private ZipEntry getEntry(final String absolutePath, final String relativePath) {
        ZipEntry entry = this.zipFile.getEntry(absolutePath);
        if(entry == null) {
            entry = this.zipFile.getEntry(relativePath);
        }
        return entry;
    }

    @Override
    public boolean isResourceChangeListenerSupported() {
        return false;
    }

    @Override
    public void registerResourceChangeListener(final ResourceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeResourceChangeListener(final ResourceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        this.zipFile.close();
    }
}
