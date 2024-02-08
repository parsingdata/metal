/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal;

import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.shl;
import static io.parsingdata.metal.Shorthand.shr;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class BitwiseValueExpressionSemanticsTest extends ParameterizedParse {

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "[170, 85] a, not(a)", simpleNot(1), stream(170, 85), enc(), true },
            { "[0, 255] a not(a)", simpleNot(1), stream(0, 255), enc(), true },
            { "[255, 0] a not(a)", simpleNot(1), stream(255, 0), enc(), true },
            { "[0, 1] a not(a)", simpleNot(1), stream(0, 1), enc(), false },
            { "[170, 85, 85, 170] a, not(a)", simpleNot(2), stream(170, 85, 85, 170), enc(), true },
            { "[255, 0, 0, 255] a not(a)", simpleNot(2), stream(255, 0, 0, 255), enc(), true },
            { "[170, 85, 0] a b and(a, b)", simpleAnd(1), stream(170, 85, 0), enc(), true },
            { "[0, 255, 0] a b and(a, b)", simpleAnd(1), stream(0, 255, 0), enc(), true },
            { "[255, 0, 0] a b and(a, b)", simpleAnd(1), stream(255, 0, 0), enc(), true },
            { "[0, 1, 1] a b and(a, b)", simpleAnd(1), stream(0, 1, 1), enc(), false },
            { "[170, 85, 85, 170, 0, 0] a b and(a, b)", simpleAnd(2), stream(170, 85, 85, 170, 0, 0), enc(), true },
            { "[0, 255, 255, 0, 0, 0] a b and(a, b)", simpleAnd(2), stream(0, 255, 255, 0, 0, 0), enc(), true },
            { "[255, 0, 0, 255, 0, 0] a b and(a, b)", simpleAnd(2), stream(255, 0, 0, 255, 0, 0), enc(), true },
            { "[170, 85, 255] a b or(a, b)", simpleOr(1), stream(170, 85, 255), enc(), true },
            { "[0, 255, 255] a b or(a, b)", simpleOr(1), stream(0, 255, 255), enc(), true },
            { "[255, 0, 255] a b or(a, b)", simpleOr(1), stream(255, 0, 255), enc(), true },
            { "[0, 1, 0] a b or(a, b)", simpleOr(1), stream(0, 1, 0), enc(), false },
            { "[170, 85, 85, 170, 255, 255] a b or(a, b)", simpleOr(2), stream(170, 85, 85, 170, 255, 255), enc(), true },
            { "[0, 255, 255, 0, 255, 255] a b or(a, b)", simpleOr(2), stream(0, 255, 255, 0, 255, 255), enc(), true },
            { "[255, 0, 0, 255, 255, 255] a b or(a, b)", simpleOr(2), stream(255, 0, 0, 255, 255, 255), enc(), true },
            { "[85, 170] a a shl 1", simpleShiftLeft(1, 1), stream(85, 170), enc(), true },
            { "[0, 85, 170, 0] a a shl 9", simpleShiftLeft(2, 9), stream(0, 85, 170, 0), enc(), true },
            { "[1, 3] a a shl 1", simpleShiftLeft(1, 1), stream(1, 3), enc(), false },
            { "[170, 85] a a shr 1", simpleShiftRight(1, 1), stream(170, 85), enc(), true },
            { "[170, 0, 0, 85] a a shr 9", simpleShiftRight(2, 9), stream(170, 0, 0, 85), enc(), true },
            { "[4, 1] a a shr 1", simpleShiftRight(1, 1), stream(4, 1), enc(), false }
        });
    }

    private static Token simpleNot(final int size) {
        return
        seq(def("a", con(size)),
            def("not(a)", con(size), eq(not(ref("a")))));
    }

    private static Token simpleAnd(final int size) {
        return
        seq(def("a", con(size)),
            def("b", con(size)),
            def("and(a, b)", con(size), eq(and(ref("a"), ref("b")))));
    }

    private static Token simpleOr(final int size) {
        return
        seq(def("a", con(size)),
            def("b", con(size)),
            def("or(a, b)", con(size), eq(or(ref("a"), ref("b")))));
    }

    private static Token simpleShiftLeft(final int size, final int shiftLeft) {
        return
        seq(def("a", con(size)),
            def("a shl " + shiftLeft, con(size), eq(shl(ref("a"), con(shiftLeft)))));
    }

    private static Token simpleShiftRight(final int size, final int shiftRight) {
        return
        seq(def("a", con(size)),
            def("a shr " + shiftRight, con(size), eq(shr(ref("a"), con(shiftRight)))));
    }

}
