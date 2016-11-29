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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.ADD_REDUCER;
import static io.parsingdata.metal.Shorthand.DIV_REDUCER;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.foldLeft;
import static io.parsingdata.metal.Shorthand.foldRight;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Util.bytesToSlice;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

/**
 * See {@link io.parsingdata.metal.ReducersTest} for other fold tests.
 */
public class FoldEdgeCaseTest {

    private static final Reducer MULTIPLE_VALUE_REDUCER = new Reducer() {
        @Override
        public ValueExpression reduce(final ValueExpression left, final ValueExpression right) {
            return ref("value");
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void valuesContainsEmpty() {
        assertTrue(foldLeft(div(con(1), con(0)), ADD_REDUCER).eval(stream(0), enc()).isEmpty());
        assertTrue(foldRight(div(con(1), con(0)), ADD_REDUCER).eval(stream(0), enc()).isEmpty());
    }

    @Test
    public void foldToEmpty() throws IOException {
        final Environment environment = rep(any("value")).parse(stream(1, 0), enc()).environment;
        assertFalse(foldLeft(ref("value"), DIV_REDUCER).eval(environment, enc()).head.isPresent());
        assertFalse(foldRight(ref("value"), DIV_REDUCER).eval(environment, enc()).head.isPresent());
    }

    @Test
    public void inputContainsEmptyInTail() {
        assertTrue(foldRight(new ValueExpression() {
            @Override
            public ImmutableList<OptionalValue> eval(Environment environment, Encoding encoding) {
                return ImmutableList.create(OptionalValue.empty()).add(OptionalValue.of(new Value(bytesToSlice(new byte[] { 1, 2 }), enc())));
            }
        }, ADD_REDUCER).eval(stream(0), enc()).isEmpty());
    }

    @Test
    public void multipleInits() throws IOException {
        final ParseResult parseResult =
            seq(
                def("init", 1),
                def("init", 1),
                def("toFold", 1),
                def("toFold", 1),
                cho(
                    def("folded", 1, eq(foldLeft(ref("toFold"), ADD_REDUCER, ref("init")))),
                    def("folded", 1, eq(foldRight(ref("toFold"), ADD_REDUCER, ref("init"))))
                )
            ).parse(stream(1, 2, 1, 2, 3), enc());

        assertFalse(parseResult.succeeded);
    }

    @Test
    public void noValues() throws IOException {
        final ParseResult parseResult =
            cho(
                def("folded", 1, eq(foldLeft(ref("toFold"), ADD_REDUCER))),
                def("folded", 1, eq(foldRight(ref("toFold"), ADD_REDUCER)))
            ).parse(stream(1), enc());

        assertFalse(parseResult.succeeded);
    }

    private void faultyReducer(final ValueExpression expression) throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Reducer must yield a single value.");

        seq(
            def("value", 1), // the reducer returns a Ref to these two values
            def("value", 1),
            def("toFold", 1),
            def("toFold", 1),
            def("folded", 1, eq(expression))
        ).parse(stream(1, 2, 1, 2, 3), enc());
    }

    @Test
    public void faultyReducerFoldLeft() throws IOException {
        faultyReducer(foldLeft(ref("toFold"), MULTIPLE_VALUE_REDUCER));
    }

    @Test
    public void faultyReducerFoldRight() throws IOException {
        faultyReducer(foldRight(ref("toFold"), MULTIPLE_VALUE_REDUCER));
    }

}
