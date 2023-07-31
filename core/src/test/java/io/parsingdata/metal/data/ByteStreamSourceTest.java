/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class ByteStreamSourceTest {

    public static final ByteStreamSource DUMMY_BYTE_STREAM_SOURCE = new ByteStreamSource(new ByteStream() {
        @Override public byte[] read(BigInteger offset, int length) throws IOException { throw new IOException("Always fails."); }
        @Override public boolean isAvailable(BigInteger offset, BigInteger length) { return true; }
    });

    @Test
    public void brokenByteStream() {
        assertThrows(UncheckedIOException.class, () -> Slice.createFromSource(DUMMY_BYTE_STREAM_SOURCE, ZERO, TEN).get().getData());
    }

}
