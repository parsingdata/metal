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
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BinaryOperator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.Shorthand;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;

/**
 * See {@link io.parsingdata.metal.ReducersTest} for other fold tests.
 */
public class FoldEdgeCaseTest {

    private static final BinaryOperator<ValueExpression> MULTIPLE_VALUE_REDUCER = (left, right) -> ref("value");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void valuesContainsEmpty() {
        assertTrue(foldLeft(div(con(1), con(0)), Shorthand::add).eval(stream(0), enc()).isEmpty());
        assertTrue(foldRight(div(con(1), con(0)), Shorthand::add).eval(stream(0), enc()).isEmpty());
    }

    @Test
    public void foldToEmpty() throws IOException {
        final ParseState parseState = rep(any("value")).parse(stream(1, 0), enc()).get();
        assertFalse(foldLeft(ref("value"), Shorthand::div).eval(parseState, enc()).head.isPresent());
        assertFalse(foldRight(ref("value"), Shorthand::div).eval(parseState, enc()).head.isPresent());
    }

    @Test
    public void inputContainsEmptyInTail() {
        assertTrue(foldRight((parseState, encoding) -> ImmutableList.create(Optional.<Value>empty()).add(Optional.of(new Value(createFromBytes(new byte[] { 1, 2 }), enc()))), Shorthand::add).eval(stream(0), enc()).isEmpty());
    }

    @Test
    public void multipleInits() throws IOException {
        final Optional<ParseState> parseResult =
            seq(
                def("init", 1),
                def("init", 1),
                def("toFold", 1),
                def("toFold", 1),
                cho(
                    def("folded", 1, eq(foldLeft(ref("toFold"), Shorthand::add, ref("init")))),
                    def("folded", 1, eq(foldRight(ref("toFold"), Shorthand::add, ref("init"))))
                )
            ).parse(stream(1, 2, 1, 2, 3), enc());

        assertFalse(parseResult.isPresent());
    }

    @Test
    public void noValues() throws IOException {
        final Optional<ParseState> parseResult =
            cho(
                def("folded", 1, eq(foldLeft(ref("toFold"), Shorthand::add))),
                def("folded", 1, eq(foldRight(ref("toFold"), Shorthand::add)))
            ).parse(stream(1), enc());

        assertFalse(parseResult.isPresent());
    }

    private void faultyReducer(final ValueExpression expression) throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Reducer must evaluate to a single value.");

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
