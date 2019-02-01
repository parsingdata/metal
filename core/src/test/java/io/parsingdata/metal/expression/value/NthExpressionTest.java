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

package io.parsingdata.metal.expression.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.expression.value.Value.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class NthExpressionTest {

    private final Token format =
        seq(
            any("valueCount"),
            repn(
                 any("value"),
                 ref("valueCount")),
            any("indexCount"),
            repn(
                 any("index"),
                 ref("indexCount")));

    private final ValueExpression nth = nth(ref("value"), ref("index"));

    @Test
    public void testEmtpyIndices() {
        // 5 values = [1, 2, 3, 4, 5], 0 indices = [], result = []
        assertFalse(makeList(stream(5, 1, 2, 3, 4, 5, 0)).isPresent());
    }

    @Test
    public void testNanIndex() {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [Nan], result = [Nan]
        final Optional<ParseState> result = format.parse(env(stream(5, 1, 2, 3, 4, 5, 0)));
        final Optional<ImmutableList<Value>> values = nth(ref("value"), div(con(0), con(0))).eval(result.get(), enc());
        assertTrue(values.isPresent());
        assertEquals(1, values.get().size);
        assertEquals(NOT_A_VALUE, values.get().head);
    }

    @Test
    public void testEmptyValuesSingleIndex() {
        // 0 values = [], 1 index = [0], result = [Nan]
        final Optional<ImmutableList<Value>> values = makeList(stream(0, 1, 0));
        assertFalse(values.isPresent());
    }

    @Test
    public void testNonExistingValueAtIndex() {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [42], result = [Nan]
        final Optional<ImmutableList<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, 42));
        assertTrue(values.isPresent());
        assertEquals(1, values.get().size);
        assertEquals(NOT_A_VALUE, values.get().head);
    }

    @Test
    public void testNegativeIndex() {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [-1], result = [Nan]
        final Optional<ImmutableList<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, -1));
        assertTrue(values.isPresent());
        assertEquals(1, values.get().size);
        assertEquals(NOT_A_VALUE, values.get().head);
    }

    @Test
    public void testSingleIndex() {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [0], result = [1]
        final Optional<ImmutableList<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, 0));
        assertTrue(values.isPresent());
        assertEquals(1, values.get().size);
        assertEquals(1, values.get().head.asNumeric().intValueExact());
    }

    @Test
    public void testMultipleIndices() {
        // 5 values = [1, 2, 3, 4, 5], 2 indices = [0, 2], result = [1, 3]
        final Optional<ImmutableList<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 2, 0, 2));
        assertTrue(values.isPresent());
        assertEquals(2, values.get().size);
        assertEquals(3, values.get().head.asNumeric().intValueExact());
        assertEquals(1, values.get().tail.head.asNumeric().intValueExact());
    }

    @Test
    public void testMultipleIndicesMixedOrder() {
        // 5 values = [5, 6, 7, 8, 9], 4 indices = [3, 2, 0, 4], result = [8, 7, 5, 9]
        final Optional<ImmutableList<Value>> values = makeList(stream(5, 5, 6, 7, 8, 9, 4, 3, 2, 0, 4));
        assertTrue(values.isPresent());
        assertEquals(4, values.get().size);
        assertEquals(9, values.get().head.asNumeric().intValueExact());
        assertEquals(5, values.get().tail.head.asNumeric().intValueExact());
        assertEquals(7, values.get().tail.tail.head.asNumeric().intValueExact());
        assertEquals(8, values.get().tail.tail.tail.head.asNumeric().intValueExact());
    }

    @Test
    public void testMixedExistingNonExistingIndices() {
        // 5 values = [1, 2, 3, 4, 5], 3 indices = [0, 42, 2], result = [1, Nan, 3]
        final Optional<ImmutableList<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 3, 0, 42, 2));
        assertTrue(values.isPresent());
        assertEquals(3, values.get().size);
        assertEquals(3, values.get().head.asNumeric().intValueExact());
        assertEquals(NOT_A_VALUE, values.get().tail.head);
        assertEquals(1, values.get().tail.tail.head.asNumeric().intValueExact());
    }

    @Test
    public void testResultLengthEqualsIndicesLength() {
        // 1 value = [1], 5 indices = [1, 2, 3, 4, 5], result = [Nan, Nan, Nan, Nan, Nan]
        final Optional<ImmutableList<Value>> values = makeList(stream(1, 1, 5, 1, 2, 3, 4, 5));
        assertTrue(values.isPresent());
        assertEquals(5, values.get().size);
        ImmutableList<Value> unpacked = values.get();
        while (!unpacked.isEmpty()) {
            assertEquals(NOT_A_VALUE, unpacked.head);
            unpacked = unpacked.tail;
        }
    }

    private Optional<ImmutableList<Value>> makeList(final ParseState parseState) {
        final Optional<ParseState> result = format.parse(env(parseState, signed()));
        assertTrue(result.isPresent());
        return nth.eval(result.get(), signed());
    }

}
