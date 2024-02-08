/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal.data;

import static java.math.BigInteger.TEN;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import io.parsingdata.metal.util.InMemoryByteStream;

public class ByteStreamSourceTest {

    public static final ByteStreamSource DUMMY_BYTE_STREAM_SOURCE = new ByteStreamSource(new ByteStream() {
        @Override public byte[] read(BigInteger offset, int length) throws IOException { throw new IOException("Always fails."); }
        @Override public boolean isAvailable(BigInteger offset, BigInteger length) { return true; }
    });

    @Test
    public void brokenByteStream() {
        assertThrows(UncheckedIOException.class, () -> Slice.createFromSource(DUMMY_BYTE_STREAM_SOURCE, ZERO, TEN).get().getData());
    }

    @Test
    @Timeout(value=1)
    public void byteStreamSourceRead() {
        // Create a large array with random data
        final int arraySize = 5_120_000;
        final byte[] bytes = new byte[arraySize];
        new Random().nextBytes(bytes);

        // Create a value that has a ConcatenatedValueSource as source.
        final ByteStreamSource source = new ByteStreamSource(new InMemoryByteStream(bytes));

        // Read from the source in small parts.
        final int readSize = 512;
        final byte[] valueBytes = new byte[arraySize];
        for (int part = 0; part < arraySize / readSize; part++) {
            final byte[] data = source.getData(valueOf(readSize * part), valueOf(readSize));
            System.arraycopy(data, 0, valueBytes, readSize * part, data.length);
        }

        // Make sure we read the data correctly.
        assertArrayEquals(bytes, valueBytes);
    }

}
