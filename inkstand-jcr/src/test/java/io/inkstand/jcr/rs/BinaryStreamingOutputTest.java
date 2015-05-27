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

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class BinaryStreamingOutputTest {

    /**
     * SLF4J Logger for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(BinaryStreamingOutputTest.class);

    @Mock
    private Binary binary;
    private BinaryStreamingOutput subject;
    private Random random;

    @Parameter
    public int dataLength;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.subject = new BinaryStreamingOutput(binary);
        this.random = new Random(System.currentTimeMillis());
    }

    /**
     * Generates a byte array of the given size with random data
     * 
     * @param dataSize
     *            the size of the array
     * @return the array with random data
     */
    protected byte[] generateData(final int dataSize) {
        final byte[] data = new byte[dataSize];
        this.random.nextBytes(data);
        return data;
    }

    /**
     * Creates a random data sequence and sets up the {@link Binary} mock
     * 
     * @param dataLength
     *            the length of the binary data to generate
     * @return the generated byte array
     * @throws RepositoryException
     */
    protected byte[] setupBinaryData(final int dataLength) throws RepositoryException {
        final byte[] inputData = generateData(dataLength);
        when(binary.getStream()).thenReturn(new ByteArrayInputStream(inputData));
        return inputData;
    }

    @Test
    public void testWrite() throws RepositoryException, IOException {

        // prepare
        final byte[] inputData = setupBinaryData(dataLength);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // act
        final long start = System.currentTimeMillis();
        subject.write(baos);
        final long end = System.currentTimeMillis();
        final byte[] writtenData = baos.toByteArray();

        // assert
        assertArrayEquals(inputData, writtenData);

        LOG.info("{} Bytes written in {} ms", this.dataLength, end - start);
    }

    @Test(expected = WebApplicationException.class)
    public void testWriteFail() throws Exception {
        //prepare
        final Binary bin = mock(Binary.class);
        final OutputStream os = mock(OutputStream.class);
        when(bin.getStream()).thenThrow(RepositoryException.class);

        final BinaryStreamingOutput bso = new BinaryStreamingOutput(bin);

        //act
        bso.write(os);

    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { 1024 }, { 100 * 1024 }, { 1024 * 1024 }, { 10 * 1024 * 1024 },
                { 30 * 1024 * 1024 } });
    }

}
