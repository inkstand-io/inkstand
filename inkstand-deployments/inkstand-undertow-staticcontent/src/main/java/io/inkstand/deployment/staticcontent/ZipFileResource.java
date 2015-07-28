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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.util.ETag;
import io.undertow.util.MimeMappings;

/**
 * A resource implementation that reflects a single entry in a zip file. The resource is bound to a {@link ZipFile} and
 * a path within that file. Created by Gerald on 26.07.2015.
 */
public class ZipFileResource implements Resource {

    private static final Logger LOG = getLogger(ZipFileResource.class);

    /**
     * The zip file that contains the entry referenced by path. It is required to read the content of the entry.
     */
    private final ZipFile zipFile;

    /**
     * The zip entry that refers to the actual resource
     */
    private final ZipEntry zipEntry;


    public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("EEE, dd MMM yyyy HH:mm:ss zzz");
    private final String path;

    /**
     * Creates a ZipFileResource for a zip entry in a zip file.
     * @param zipFile
     *  the zip file containing the zip entry. The file is needed to read the actual data from the file.
     * @param zipEntry
     *  the actual zip entry that identifies the resource within the zip file.
     * @param path
     *  the requested path of the resource. As zip file entries may be named absolute or relative, they may
     *  actually differ from the requested path.
     */
    public ZipFileResource(ZipFile zipFile, final ZipEntry zipEntry, String path) {

        this.zipFile = zipFile;
        this.zipEntry = zipEntry;
        this.path = path;
    }


    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Date getLastModified() {

        return new Date(zipEntry.getTime());
    }

    @Override
    public String getLastModifiedString() {
        return DATE_FORMAT.format(getLastModified());
    }

    @Override
    public ETag getETag() {

        return null;
    }

    @Override
    public String getName() {

        return zipEntry.getName();
    }

    @Override
    public boolean isDirectory() {

        return zipEntry.isDirectory();
    }

    @Override
    public List<Resource> list() {

        if(!zipEntry.isDirectory()){
            return Collections.emptyList();
        }

        final List<Resource> resourceList = new ArrayList<>();
        final Enumeration<? extends ZipEntry> entries = this.zipFile.entries();
        final String rootEntryPath = zipEntry.getName();
        while(entries.hasMoreElements()){
            final ZipEntry entry = entries.nextElement();
            final String entryPath = entry.getName();

            //we make just a check for the base path. With this, all entries including those in the subdirectories
            //will be added too. One could consider this as a characteristic of a zip-file content store. If this
            //causes problems, this part should be improved with a more sophisticated logic
            if(!entryPath.equals(rootEntryPath) && entryPath.startsWith(rootEntryPath)){
                resourceList.add(new ZipFileResource(this.zipFile, entry, entryPath));
            }
        }

        return resourceList;
    }

    @Override
    public String getContentType(final MimeMappings mimeMappings) {

        String extension = zipEntry.getName().substring(zipEntry.getName().lastIndexOf('.') + 1);
        return mimeMappings.getMimeType(extension);

    }

    @Override
    public void serve(final Sender sender, final HttpServerExchange exchange, final IoCallback completionCallback) {

        final OutputStream outStream = exchange.getOutputStream();
        try (InputStream inStream = zipFile.getInputStream(zipEntry)) {

            IOUtils.copy(inStream, outStream);
            completionCallback.onComplete(exchange, sender);

        } catch (IOException e) {
            LOG.error("Could not serve content file", e);
            completionCallback.onException(exchange, sender, e);
        }
    }

    @Override
    public Long getContentLength() {

        return zipEntry.getSize();
    }

    @Override
    public String getCacheKey() {

        return null;
    }

    @Override
    public File getFile() {

        return null;
    }

    @Override
    public File getResourceManagerRoot() {

        return null;
    }

    @Override
    public URL getUrl() {

        return null;
    }
}
