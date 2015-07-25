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

package io.inkstand.jcr.rs;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Streams binary data from a JCR {@link Binary} property value to an output stream.
 */
public class BinaryStreamingOutput implements StreamingOutput {

    private final Binary data;

    public BinaryStreamingOutput(final Binary bin) {
        this.data = bin;
    }

    @Override
    public void write(final OutputStream paramOutputStream)
            throws IOException, WebApplicationException {

        try (InputStream inStream = data.getStream()){
            int data;
            while ((data = inStream.read()) != -1) {
                paramOutputStream.write(data);
            }
        } catch (final RepositoryException e) {
            throw new WebApplicationException(e);

        }
    }

}
