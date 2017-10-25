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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ImmutableList;
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
    public void testEmtpyIndices() throws IOException {
        // 5 values = [1, 2, 3, 4, 5], 0 indices = [], result = []
        makeList(stream(5, 1, 2, 3, 4, 5, 0), 0);
    }

    @Test
    public void testNanIndex() throws IOException {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [Nan], result = [Nan]
        final Optional<ParseState> result = format.parse(stream(5, 1, 2, 3, 4, 5, 0), enc());
        final ImmutableList<Optional<Value>> values = nth(ref("value"), div(con(0), con(0))).eval(result.get(), enc());
        assertThat(values.size, is(equalTo(1L)));
        assertThat(values.head.isPresent(), is(equalTo(false)));
    }

    @Test
    public void testEmptyValuesSingleIndex() throws IOException {
        // 0 values = [], 1 index = [0], result = [Nan]
        final ImmutableList<Optional<Value>> values = makeList(stream(0, 1, 0), 1);
        assertThat(values.head.isPresent(), is(equalTo(false)));
    }

    @Test
    public void testNonExistingValueAtIndex() throws IOException {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [42], result = [Nan]
        final ImmutableList<Optional<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, 42), 1);
        assertThat(values.head.isPresent(), is(equalTo(false)));
    }

    @Test
    public void testNegativeIndex() throws IOException {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [-1], result = [Nan]
        final ImmutableList<Optional<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, -1), 1);
        assertThat(values.head.isPresent(), is(equalTo(false)));
    }

    @Test
    public void testSingleIndex() throws IOException {
        // 5 values = [1, 2, 3, 4, 5], 1 index = [0], result = [1]
        final ImmutableList<Optional<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 1, 0), 1);
        assertThat(values.head.get().asNumeric().intValueExact(), is(equalTo(1)));
    }

    @Test
    public void testMultipleIndices() throws IOException {
        // 5 values = [1, 2, 3, 4, 5], 2 indices = [0, 2], result = [1, 3]
        final ImmutableList<Optional<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 2, 0, 2), 2);
        assertThat(values.head.get().asNumeric().intValueExact(), is(equalTo(3)));
        assertThat(values.tail.head.get().asNumeric().intValueExact(), is(equalTo(1)));
    }

    @Test
    public void testMultipleIndicesMixedOrder() throws IOException {
        // 5 values = [5, 6, 7, 8, 9], 4 indices = [3, 2, 0, 4], result = [8, 7, 5, 9]
        final ImmutableList<Optional<Value>> values = makeList(stream(5, 5, 6, 7, 8, 9, 4, 3, 2, 0, 4), 4);
        assertThat(values.head.get().asNumeric().intValueExact(), is(equalTo(9)));
        assertThat(values.tail.head.get().asNumeric().intValueExact(), is(equalTo(5)));
        assertThat(values.tail.tail.head.get().asNumeric().intValueExact(), is(equalTo(7)));
        assertThat(values.tail.tail.tail.head.get().asNumeric().intValueExact(), is(equalTo(8)));
    }

    @Test
    public void testMixedExistingNonExistingIndices() throws IOException {
        // 5 values = [1, 2, 3, 4, 5], 3 indices = [0, 42, 2], result = [1, Nan, 3]
        final ImmutableList<Optional<Value>> values = makeList(stream(5, 1, 2, 3, 4, 5, 3, 0, 42, 2), 3);
        assertThat(values.head.get().asNumeric().intValueExact(), is(equalTo(3)));
        assertThat(values.tail.head.isPresent(), is(equalTo(false)));
        assertThat(values.tail.tail.head.get().asNumeric().intValueExact(), is(equalTo(1)));
    }

    @Test
    public void testResultLengthEqualsIndicesLength() throws IOException {
        // 1 value = [1], 8 indices = [1, 2, 3, 4, 5, 6, 7, 8], result = [Nan, Nan, Nan, Nan, Nan]
        final ImmutableList<Optional<Value>> values = makeList(stream(1, 1, 5, 1, 2, 3, 4, 5), 5);
        assertNonePresent(values);
    }

    private void assertNonePresent(final ImmutableList<Optional<Value>> inputValues) {
        ImmutableList<Optional<Value>> values = inputValues;
        while (!values.isEmpty()) {
            assertThat(values.head.isPresent(), is(equalTo(false)));
            values = values.tail;
        }
    }

    private ImmutableList<Optional<Value>> makeList(final ParseState parseState, final long listSize) throws IOException {
        final Optional<ParseState> result = format.parse(parseState, signed());
        assertTrue(result.isPresent());
        final ImmutableList<Optional<Value>> values = nth.eval(result.get(), signed());
        assertThat(values.size, is(equalTo(listSize)));
        return values;
    }

}
