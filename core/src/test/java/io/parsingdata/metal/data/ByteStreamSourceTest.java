/*
 * Copyright 2013-2016 Netherlands Forensic Institute
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ByteStreamSourceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void brokenByteStream() {
        thrown.expect(UncheckedIOException.class);
        Slice.createFromSource(new ByteStreamSource(new ByteStream() {
            @Override public byte[] read(long offset, int length) throws IOException { throw new IOException("Always fails."); }
            @Override public boolean isAvailable(long offset, int length) { return true; }
        }), 0, BigInteger.TEN).get().getData();
    }

}
