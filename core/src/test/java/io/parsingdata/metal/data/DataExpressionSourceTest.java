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

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.data.Slice.createFromSource;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParseStateFactory;

public class DataExpressionSourceTest {

    public ParseValue setupValue() {
        final Optional<ParseState> result = setupResult();
        assertTrue(result.isPresent());
        return getValue(result.get().order, "b");
    }

    private Optional<ParseState> setupResult() {
        final Token token =
            seq(def("a", con(4)),
                tie(def("b", con(2)), ref("a")));
        return token.parse(env(stream(1, 2, 3, 4)));
    }

    @Test
    public void createSliceFromParseValue() {
        final ParseValue value = setupValue();
        assertTrue(value.slice().source.isAvailable(ZERO, BigInteger.valueOf(4)));
        assertFalse(value.slice().source.isAvailable(ZERO, BigInteger.valueOf(5)));
    }

    @Test
    public void indexOutOfBounds() {
        final Optional<ParseState> result = setupResult();
        final DataExpressionSource source = new DataExpressionSource(ref("a"), 1, result.get(), enc());

        final Exception e = Assertions.assertThrows(IllegalStateException.class, () -> source.getData(ZERO, BigInteger.valueOf(4)));
        assertEquals("ValueExpression dataExpression yields 1 result(s) (expected at least 2).", e.getMessage());
    }

    @Test
    public void notAValue() {
        final Exception e = Assertions.assertThrows(IllegalStateException.class, () -> new DataExpressionSource(div(con(1), con(0)), 0, EMPTY_PARSE_STATE, enc()).isAvailable(ZERO, ZERO));
        assertEquals("ValueExpression dataExpression yields NOT_A_VALUE at index 0.", e.getMessage());
    }

    @Test
    @Timeout(value=1)
    public void dataExpressionSourceRead() {
        // Create a large array with random data
        final int arraySize = 5_120_000;
        final byte[] bytes = new byte[arraySize];
        new Random().nextBytes(bytes);

        // Split the data in separate CoreValues.
        final ParseValue parseValue = new ParseValue("test", def("def", bytes.length), Slice.createFromBytes(bytes), Encoding.DEFAULT_ENCODING);
        final ParseState parseState = stream("some parse state", UTF_8).add(parseValue);

        // Create a DataExpressionSource to read from.
        final DataExpressionSource source = new DataExpressionSource(ref("test"), 0, parseState, Encoding.DEFAULT_ENCODING);

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
