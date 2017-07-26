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

package io.parsingdata.metal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.EMPTY;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.EMPTY_VE;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class DefSizeTest {
    public static final Token FORMAT =
        seq(
            def("length", con(4)),
            def("data", ref("length"))
        );

    @Test
    public void testValidLength() throws IOException {
        final ByteStream stream = new InMemoryByteStream(new byte[]{
            0x00, 0x00, 0x00, 0x02, // length = 2
            0x04, 0x08
        });
        final Optional<Environment> result = FORMAT.parse(new Environment(stream), new Encoding());

        assertTrue(result.isPresent());
        assertArrayEquals(
            new byte[]{0x04, 0x08},
            getValue(result.get().order, "data").getValue()
        );
    }

    @Test
    public void testInvalidLength() throws IOException {
        final ByteStream stream = new InMemoryByteStream(new byte[]{
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, // length = -1
            0x04, 0x08
        });
        final Optional<Environment> result = FORMAT.parse(new Environment(stream), new Encoding());

        assertFalse(result.isPresent());
    }

    @Test
    public void testEmptyLengthInList() throws IOException {
        assertFalse(def("a", EMPTY_VE).parse(stream(1, 2, 3, 4), enc()).isPresent());
        final Token aList = seq(any("a"), any("a"));
        assertFalse(seq(aList, def("b", ref("a"))).parse(stream(1, 2, 3, 4), enc()).isPresent());
    }

    @Test
    public void zeroSizeDef() throws IOException {
        assertTrue(seq(
            def("twentyone", con(1), eq(con(21))),
            EMPTY,
            def("fortytwo", con(1), eq(con(42)))).parse(stream(21, 42), enc()).isPresent());
    }

}
