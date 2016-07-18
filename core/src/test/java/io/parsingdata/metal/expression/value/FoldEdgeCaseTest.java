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

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.foldLeft;
import static io.parsingdata.metal.Shorthand.foldRight;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.ParseResult;

/**
 * See {@link io.parsingdata.metal.ReducersTest} for other fold tests.
 */
public class FoldEdgeCaseTest {

    private final static Reducer ADD_REDUCER = new Reducer() {
        @Override
        public ValueExpression reduce(final ValueExpression l, final ValueExpression r) {
            return add(l, r);
        }
    };

    private static final Reducer MULTIPLE_VALUE_REDUCER = new Reducer() {
        @Override
        public ValueExpression reduce(final ValueExpression l, final ValueExpression r) {
            return ref("value");
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

        assertFalse(parseResult.succeeded());
    }

    @Test
    public void noValues() throws IOException {
        final ParseResult parseResult =
            cho(
                def("folded", 1, eq(foldLeft(ref("toFold"), ADD_REDUCER))),
                def("folded", 1, eq(foldRight(ref("toFold"), ADD_REDUCER)))
            ).parse(stream(1), enc());

        assertFalse(parseResult.succeeded());
    }

    @Test
    public void faultyReducerFoldLeft() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Reducer must yield a single value.");

        seq(
            def("value", 1), // the reducer returns a Ref to these two values
            def("value", 1),
            def("toFold", 1),
            def("toFold", 1),
            def("folded", 1, eq(foldLeft(ref("toFold"), MULTIPLE_VALUE_REDUCER)))
        ).parse(stream(1, 2, 1, 2, 3), enc());
    }

    @Test
    public void faultyReducerFoldRight() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Reducer must yield a single value.");

        seq(
            def("value", 1), // the reducer returns a Ref to these two values
            def("value", 1),
            def("toFold", 1),
            def("toFold", 1),
            def("folded", 1, eq(foldRight(ref("toFold"), MULTIPLE_VALUE_REDUCER)))
        ).parse(stream(1, 2, 1, 2, 3), enc());
    }

}
