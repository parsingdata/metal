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

package io.parsingdata.metal.expression.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.AutoEqualityTest.DUMMY_STREAM;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseValue;

public class ExpandTest {

    public static final int SIZE = 5;
    public static final int VALUE_1 = 42;
    public static final int VALUE_2 = 84;

    public static final ParseValue PARSEVALUE_1 = createParseValue("a", VALUE_1);
    public static final ParseValue PARSEVALUE_2 = createParseValue("a", VALUE_2);

    @Test
    public void expandEmpty() {
        final ImmutableList<Value> result = exp(ref("a"), con(5)).eval(EMPTY_PARSE_STATE, enc());
        assertTrue(result.isEmpty());
    }

    @Test
    public void expandNotAValueTimes() {
        final Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> exp(con(1), div(con(1), con(0))).eval(EMPTY_PARSE_STATE, enc()));
        assertEquals("Count must evaluate to a non-empty countable value.", e.getMessage());
    }

    @Test
    public void expandEmptyTimes() {
        final Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> exp(con(1), last(ref("a"))).eval(EMPTY_PARSE_STATE, enc()));
        assertEquals("Count must evaluate to a non-empty countable value.", e.getMessage());
    }

    @Test
    public void expandZeroTimes() {
        final ImmutableList<Value> result = exp(con(1), con(0)).eval(EMPTY_PARSE_STATE, enc());
        assertTrue(result.isEmpty());
    }

    @Test
    public void expandValue() {
        ImmutableList<Value> result = exp(con(VALUE_1), con(SIZE)).eval(EMPTY_PARSE_STATE, enc());
        assertEquals(SIZE, (long) result.size());
        for (int i = 0; i < SIZE; i++) {
            assertEquals(VALUE_1, result.head().asNumeric().intValueExact());
            result = result.tail();
        }
    }

    @Test
    public void expandList() {
        ImmutableList<Value> result = exp(ref("a"), con(SIZE)).eval(createFromByteStream(DUMMY_STREAM).add(PARSEVALUE_2).add(PARSEVALUE_1), enc());
        assertEquals(2 * SIZE, (long) result.size());
        for (int i = 0; i < SIZE; i++) {
            assertEquals(VALUE_1, result.head().asNumeric().intValueExact());
            result = result.tail();
            assertEquals(VALUE_2, result.head().asNumeric().intValueExact());
            result = result.tail();
        }
    }

    public static ParseValue createParseValue(String name, int value) {
        return new ParseValue(name, any(name), createFromBytes(new byte[] { (byte)value }), enc());
    }

}
