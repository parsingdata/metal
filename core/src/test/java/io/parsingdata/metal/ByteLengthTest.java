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

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.len;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.encoding.ByteOrder.LITTLE_ENDIAN;
import static io.parsingdata.metal.encoding.Sign.UNSIGNED;
import static io.parsingdata.metal.util.EnvironmentFactory.env;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class ByteLengthTest {

    private static final Encoding ENCODING = new Encoding(UNSIGNED, UTF_8, LITTLE_ENDIAN);

    // Note that this token does not make sense,
    // but Len will become useful when Let is implemented
    private static final Token STRING = seq(
        def("length", 1),
        def("text1", last(ref("length"))),
        def("text2", len(last(ref("text1")))));
    //  let("hasText", con(true), ltNum(len(ref("text1")), con(0))));

    private static final Token NAME =
        def("name", 4, ltNum(len(ref("no_such_ref")), con(0)));

    @Test
    public void testLen() {
        final byte[] text1 = string("Hello");
        final byte[] text2 = "Metal".getBytes(UTF_8);

        final ByteStream stream = new InMemoryByteStream(concat(text1, text2));
        final ParseState parseState = createFromByteStream(stream);
        final Optional<ParseState> result = STRING.parse(env(parseState, ENCODING));

        assertTrue(result.isPresent());
        final ParseGraph graph = result.get().order;
        assertEquals(5, getValue(graph, "length").asNumeric().byteValueExact());
        assertEquals("Hello", getValue(graph, "text1").asString());
        assertEquals("Metal", getValue(graph, "text2").asString());
    }

    @Test
    public void testLenNull() {
        final ByteStream stream = new InMemoryByteStream(string("Joe"));
        final ParseState parseState = createFromByteStream(stream);
        final Optional<ParseState> result = NAME.parse(env(parseState, ENCODING));
        assertFalse(result.isPresent());
    }

    private byte[] string(final String text) {
        final byte[] data = text.getBytes(UTF_8);
        final byte[] length = {(byte) data.length};
        return concat(length, data);
    }

    private byte[] concat(final byte[] b1, final byte[] b2) {
        final byte[] result = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, result, 0, b1.length);
        System.arraycopy(b2, 0, result, b1.length, b2.length);
        return result;
    }
}
