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
import java.io.PrintStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.util.ETag;
import io.undertow.util.MimeMappings;

/**
 * Created by Gerald on 26.07.2015.
 */
public class ZipFileResource implements Resource {

    private static final Logger LOG = getLogger(ZipFileResource.class);

    private final ZipFile zipFile;
    private final ZipEntry zipEntry;
    private final String path;

    public ZipFileResource(ZipFile zipFile, String path){
        this.zipFile = zipFile;
        this.zipEntry = zipFile.getEntry(path);
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

        return null;
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

        return null;
    }

    @Override
    public String getContentType(final MimeMappings mimeMappings) {

        return null;
    }

    @Override
    public void serve(final Sender sender, final HttpServerExchange exchange, final IoCallback completionCallback) {

        OutputStream os = exchange.getOutputStream();
        try(InputStream is = zipFile.getInputStream(zipEntry)){

            IOUtils.copy(is, os);

        } catch (IOException e) {
            LOG.error("Could not serve content file", e);
            e.printStackTrace(new PrintStream(os));
        } finally {
            completionCallback.onComplete(exchange, sender);
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
