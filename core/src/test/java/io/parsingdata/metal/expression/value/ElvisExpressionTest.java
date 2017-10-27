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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.elvis;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class ElvisExpressionTest {

    private final Token choice = cho(
        def("a", 1, eq(con(1))),
        def("b", 1, eq(con(2)))
    );

    private final ValueExpression elvisExpression = elvis(ref("a"), ref("b"));

    @Test
    public void elvisLeft() throws IOException { // the building
        final Optional<ParseState> result = choice.parse(env(stream(1)));
        final ImmutableList<Optional<Value>> eval = elvisExpression.eval(result.get(), enc());

        assertNotNull(eval);
        assertEquals(1, eval.size);
        assertThat(eval.head.get().asNumeric().intValueExact(), is(equalTo(1)));
    }

    @Test
    public void elvisRight() throws IOException {
        final Optional<ParseState> result = choice.parse(env(stream(2)));
        final ImmutableList<Optional<Value>> eval = elvisExpression.eval(result.get(), enc());

        assertNotNull(eval);
        assertEquals(1, eval.size);
        assertThat(eval.head.get().asNumeric().intValueExact(), is(equalTo(2)));
    }

    @Test
    public void elvisNone() throws IOException {
        final ImmutableList<Optional<Value>> eval = elvisExpression.eval(EMPTY_PARSE_STATE, enc());

        assertNotNull(eval);
        assertTrue(eval.isEmpty());
    }

    @Test
    public void elvisList() throws IOException {
        final Optional<ParseState> result = seq(any("a"), any("a"), any("b"), any("b")).parse(env(stream(1, 2, 3, 4)));
        assertTrue(result.isPresent());
        final ValueExpression elvis = elvis(ref("a"), ref("b"));
        final ImmutableList<Optional<Value>> eval = elvis.eval(result.get(), enc());
        assertEquals(2, eval.size);
        assertEquals(2, eval.head.get().asNumeric().intValueExact());
        assertEquals(1, eval.tail.head.get().asNumeric().intValueExact());
    }

    @Test
    public void elvisListWithEmpty() throws IOException {
        final Optional<ParseState> result = seq(any("a"), any("a"), any("b"), any("b")).parse(env(stream(1, 2, 3, 4)));
        assertTrue(result.isPresent());
        final ValueExpression elvis = elvis(ref("c"), ref("b"));
        final ImmutableList<Optional<Value>> eval = elvis.eval(result.get(), enc());
        assertEquals(2, eval.size);
        assertEquals(4, eval.head.get().asNumeric().intValueExact());
        assertEquals(3, eval.tail.head.get().asNumeric().intValueExact());
    }

    @Test
    public void elvisListDifferentLengths() throws IOException {
        final Optional<ParseState> result = seq(any("a"), any("a"), any("b"), any("b"), any("b")).parse(env(stream(1, 2, 3, 4, 5)));
        assertTrue(result.isPresent());
        final ValueExpression elvis = elvis(ref("a"), ref("b"));
        final ImmutableList<Optional<Value>> eval = elvis.eval(result.get(), enc());
        assertEquals(3, eval.size);
        assertEquals(2, eval.head.get().asNumeric().intValueExact());
        assertEquals(1, eval.tail.head.get().asNumeric().intValueExact());
        assertEquals(3, eval.tail.tail.head.get().asNumeric().intValueExact());
    }

    @Test
    public void elvisListEmpty() {
        final ValueExpression elvis = elvis(ref("a"), ref("b"));
        final ImmutableList<Optional<Value>> eval = elvis.eval(stream(0), enc());
        assertEquals(0, eval.size);
    }

    @Test
    public void elvisLeftNone() {
        final ValueExpression elvis = elvis(div(con(1), con(0)), con(1));
        final ImmutableList<Optional<Value>> eval = elvis.eval(stream(0), enc());
        assertEquals(1, eval.size);
        assertEquals(1, eval.head.get().asNumeric().intValueExact());
    }

    @Test
    public void toStringTest() {
        assertThat(elvisExpression.toString(), is(equalTo("Elvis(NameRef(a),NameRef(b))")));
    }

}
