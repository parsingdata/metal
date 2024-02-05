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
import static org.junit.jupiter.api.Assertions.assertFalse;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.foldLeft;
import static io.parsingdata.metal.Shorthand.foldRight;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;
import java.util.function.BinaryOperator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.Shorthand;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;

/**
 * See {@link io.parsingdata.metal.ReducersTest} for other fold tests.
 */
public class FoldEdgeCaseTest {

    private static final BinaryOperator<SingleValueExpression> EMPTY_VALUE_REDUCER = (left, right) -> (parseState, encoding) -> Optional.empty();

    @Test
    public void valuesContainsEmpty() {
        assertEquals(NOT_A_VALUE, foldLeft(div(con(1), con(0)), Shorthand::add).eval(stream(0), enc()).head());
        assertEquals(NOT_A_VALUE, foldRight(div(con(1), con(0)), Shorthand::add).eval(stream(0), enc()).head());
    }

    @Test
    public void foldToEmpty() {
        final ParseState parseState = rep(any("value")).parse(env(stream(1, 0))).get();
        final ImmutableList<Value> foldLeftNan = foldLeft(ref("value"), Shorthand::div).eval(parseState, enc());
        assertEquals(1, (long) foldLeftNan.size());
        assertEquals(NOT_A_VALUE, foldLeftNan.head());
        final ImmutableList<Value> foldRightNan = foldRight(ref("value"), Shorthand::div).eval(parseState, enc());
        assertEquals(1, (long) foldRightNan.size());
        assertEquals(NOT_A_VALUE, foldRightNan.head());
    }

    @Test
    public void inputContainsEmptyInTail() {
        assertEquals(NOT_A_VALUE, foldRight((parseState, encoding) -> ImmutableList.create(NOT_A_VALUE).addHead(new CoreValue(createFromBytes(new byte[]{1, 2}), enc())), Shorthand::add).eval(stream(0), enc()).head());
    }

    @Test
    public void notAValueInit() {
        final ImmutableList<Value> result = fold(exp(con(1), con(2)), Shorthand::add, con(NOT_A_VALUE)).eval(EMPTY_PARSE_STATE, DEFAULT_ENCODING);
        assertEquals(1, (long) result.size());
        assertEquals(NOT_A_VALUE, result.head());
    }

    @Test
    public void noValues() {
        final Optional<ParseState> parseResult =
            cho(
                def("folded", 1, eq(foldLeft(ref("toFold"), Shorthand::add))),
                def("folded", 1, eq(foldRight(ref("toFold"), Shorthand::add)))
            ).parse(env(stream(1)));

        assertFalse(parseResult.isPresent());
    }

    private void faultyReducer(final ValueExpression expression) {
        final Exception e = Assertions.assertThrows(IllegalArgumentException.class, () ->
            seq(
                def("value", 1), // the reducer returns a Ref to these two values
                def("value", 1),
                def("toFold", 1),
                def("toFold", 1),
                def("folded", 1, eq(expression))
            ).parse(env(stream(1, 2, 1, 2, 3)))
        );
        assertEquals("Reducer must evaluate to a value.", e.getMessage());

    }

    @Test
    public void faultyReducerFoldLeft() {
        faultyReducer(foldLeft(ref("toFold"), EMPTY_VALUE_REDUCER));
    }

    @Test
    public void faultyReducerFoldRight() {
        faultyReducer(foldRight(ref("toFold"), EMPTY_VALUE_REDUCER));
    }

}
