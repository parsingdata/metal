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

package io.parsingdata.metal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.EMPTY;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.EMPTY_SVE;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class DefSizeTest {
    public static final Token FORMAT =
        seq(
            def("length", con(4)),
            def("data", last(ref("length")))
        );

    @Test
    public void testValidLength() {
        final ByteStream stream = new InMemoryByteStream(new byte[]{
            0x00, 0x00, 0x00, 0x02, // length = 2
            0x04, 0x08
        });
        final Optional<ParseState> result = FORMAT.parse(env(createFromByteStream(stream), enc()));

        assertTrue(result.isPresent());
        assertArrayEquals(
            new byte[]{0x04, 0x08},
            getValue(result.get().order, "data").value()
        );
    }

    @Test
    public void testInvalidLength() {
        final ByteStream stream = new InMemoryByteStream(new byte[]{
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, // length = -1
            0x04, 0x08
        });
        final Optional<ParseState> result = FORMAT.parse(env(createFromByteStream(stream), signed()));

        assertFalse(result.isPresent());
    }

    @Test
    public void testLengthNotAValue() {
        assertFalse(def("a", EMPTY_SVE).parse(env(stream(1, 2, 3, 4))).isPresent());
    }

    @Test
    public void zeroSizeDef() {
        assertTrue(seq(
            def("twentyone", con(1), eq(con(21))),
            EMPTY,
            def("fortytwo", con(1), eq(con(42)))).parse(env(stream(21, 42))).isPresent());
    }

}
