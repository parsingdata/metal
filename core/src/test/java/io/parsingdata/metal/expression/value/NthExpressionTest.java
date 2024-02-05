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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class NthExpressionTest {

    private final Token format =
        seq(
            any("valueCount"),
            repn(
                 any("value"),
                 last(ref("valueCount"))),
            any("indexCount"),
            repn(
                 any("index"),
                 last(ref("indexCount"))));

    private final ValueExpression nth = nth(ref("value"), ref("index"));

    @Test
    public void testEmtpyIndices() {
        // 5 values = [1, 2, 3, 4, 5], 0 indices = [], result = []
        assertTrue(makeList(stream(5, 1, 2, 3, 4, 5, 0)).isEmpty());
    }

    @Test
    public void testNanIndex() {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [Nan], result = [Nan]
        final Optional<ParseState> result = format.parse(env(stream(5, 1, 2, 3, 4, 5, 0)));
        final ImmutableList<Value> values = nth(ref("value"), div(con(0), con(0))).eval(result.get(), enc());
        assertEquals(1, (long) values.size());
        assertEquals(NOT_A_VALUE, values.head());
    }

    @Test
    public void testEmptyValuesSingleIndex() {
        // 0 values = [], 1 index = [0], result = [Nan]
        final ImmutableList<Value> values = makeList(stream(0, 1, 0));
        assertEquals(1, (long) values.size());
        assertEquals(NOT_A_VALUE, values.head());
    }

    @Test
    public void testNonExistingValueAtIndex() {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [42], result = [Nan]
        final ImmutableList<Value> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, 42));
        assertEquals(1, (long) values.size());
        assertEquals(NOT_A_VALUE, values.head());
    }

    @Test
    public void testNegativeIndex() {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [-1], result = [Nan]
        final ImmutableList<Value> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, -1));
        assertEquals(1, (long) values.size());
        assertEquals(NOT_A_VALUE, values.head());
    }

    @Test
    public void testSingleIndex() {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [0], result = [1]
        final ImmutableList<Value> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, 0));
        assertEquals(1, (long) values.size());
        assertEquals(1, values.head().asNumeric().intValueExact());
    }

    @Test
    public void testMultipleIndices() {
        // 5 values = [1, 2, 3, 4, 5], 2 indices = [0, 2], result = [1, 3]
        final ImmutableList<Value> values = makeList(stream(5, 1, 2, 3, 4, 5, 2, 0, 2));
        assertEquals(2, (long) values.size());
        assertEquals(3, values.head().asNumeric().intValueExact());
        assertEquals(1, values.tail().head().asNumeric().intValueExact());
    }

    @Test
    public void testMultipleIndicesMixedOrder() {
        // 5 values = [5, 6, 7, 8, 9], 4 indices = [3, 2, 0, 4], result = [8, 7, 5, 9]
        final ImmutableList<Value> values = makeList(stream(5, 5, 6, 7, 8, 9, 4, 3, 2, 0, 4));
        assertEquals(4, (long) values.size());
        assertEquals(9, values.head().asNumeric().intValueExact());
        assertEquals(5, values.tail().head().asNumeric().intValueExact());
        assertEquals(7, values.tail().tail().head().asNumeric().intValueExact());
        assertEquals(8, values.tail().tail().tail().head().asNumeric().intValueExact());
    }

    @Test
    public void testMixedExistingNonExistingIndices() {
        // 5 values = [1, 2, 3, 4, 5], 3 indices = [0, 42, 2], result = [1, Nan, 3]
        final ImmutableList<Value> values = makeList(stream(5, 1, 2, 3, 4, 5, 3, 0, 42, 2));
        assertEquals(3, (long) values.size());
        assertEquals(3, values.head().asNumeric().intValueExact());
        assertEquals(NOT_A_VALUE, values.tail().head());
        assertEquals(1, values.tail().tail().head().asNumeric().intValueExact());
    }

    @Test
    public void testResultLengthEqualsIndicesLength() {
        // 1 value = [1], 5 indices = [1, 2, 3, 4, 5], result = [Nan, Nan, Nan, Nan, Nan]
        ImmutableList<Value> values = makeList(stream(1, 1, 5, 1, 2, 3, 4, 5));
        assertEquals(5, (long) values.size());
        while (!values.isEmpty()) {
            assertEquals(NOT_A_VALUE, values.head());
            values = values.tail();
        }
    }

    private ImmutableList<Value> makeList(final ParseState parseState) {
        final Optional<ParseState> result = format.parse(env(parseState, signed()));
        assertTrue(result.isPresent());
        return nth.eval(result.get(), signed());
    }

}
