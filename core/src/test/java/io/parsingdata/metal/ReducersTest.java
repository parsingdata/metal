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

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.foldLeft;
import static io.parsingdata.metal.Shorthand.foldRight;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class ReducersTest extends ParameterizedParse {

    private final static Token reduceAddA = token(1, eq(fold(ref("a"), Shorthand::add)));
    private final static Token reduceAddOffsetA = token(1, eq(fold(offset(ref("a")), Shorthand::add)));
    private final static Token reduceAddAInit0 = token(1, eq(fold(ref("a"), Shorthand::add, con(0))));
    private final static Token reduceAddAInit1 = token(1, eq(fold(ref("a"), Shorthand::add, con(1))));
    private final static Token reduceMulA = token(1, eq(fold(ref("a"), Shorthand::mul)));
    private final static Token reduceAllAplusMulA = token(1, eq(add(fold(ref("a"), Shorthand::add), fold(ref("a"), Shorthand::mul))));
    private final static Token reduceCatA = token(3, eq(fold(ref("a"), Shorthand::cat)));
    private final static Token reduceCatAToNumDefBEPostBE = token(3, eqNum(fold(ref("a"), Shorthand::cat)), enc(), enc());
    private final static Token reduceCatAToNumDefBEPostLE = token(3, eqNum(fold(ref("a"), Shorthand::cat)), enc(), le());
    private final static Token reduceCatAToNumDefLEPostBE = token(3, eqNum(fold(ref("a"), Shorthand::cat)), le(), enc());
    private final static Token reduceCatAToNumDefLEPostLE = token(3, eqNum(fold(ref("a"), Shorthand::cat)), le(), le());
    private final static Token foldLeftSubA = token(1, eq(foldLeft(ref("a"), Shorthand::sub)));
    private final static Token foldLeftSubAInit2 = token(1, eq(foldLeft(ref("a"), Shorthand::sub, con(2))));
    private final static Token foldRightSubA = token(1, eq(foldRight(ref("a"), Shorthand::sub)));
    private final static Token foldRightSubAInit2 = token(1, eq(foldRight(ref("a"), Shorthand::sub, con(2))));

    private static Token token(final long size, final Expression pred, final Encoding encDef, final Encoding encPost) {
        return seq(any("a"),
            any("a"),
            any("a"),
            post(def("b", con(size), encDef), pred, encPost));
    }

    private static Token token(final long size, final Expression pred) {
        return token(size, pred, enc(), enc());
    }

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "[1, 2, 3, 6] a, a, a, addAll(a)", reduceAddA, stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 3] a, a, a, addAllOffset(a)", reduceAddOffsetA, stream(1, 2, 3, 3), enc(), true },
            { "[1, 2, 3, 6] a, a, a, addAll(a, 0)", reduceAddAInit0, stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 6] a, a, a, addAll(a, 1)", reduceAddAInit1, stream(1, 2, 3, 6), enc(), false },
            { "[1, 2, 3, 7] a, a, a, addAll(a)", reduceAddA, stream(1, 2, 3, 7), enc(), false },
            { "[1, 2, 3, 7] a, a, a, addAll(a, 0)", reduceAddAInit0, stream(1, 2, 3, 7), enc(), false },
            { "[1, 2, 3, 7] a, a, a, addAll(a, 1)", reduceAddAInit1, stream(1, 2, 3, 7), enc(), true },
            { "[1, 2, 3, 6] a, a, a, mulAll(a)", reduceMulA, stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 7] a, a, a, mulAll(a)", reduceMulA, stream(1, 2, 3, 7), enc(), false },
            { "[1, 2, 4, 15] a, a, a, (addAll(a)+mulAll(a))", reduceAllAplusMulA, stream(1, 2, 4, 15), enc(), true },
            { "[1, 2, 4, 16] a, a, a, (addAll(a)+mulAll(a))", reduceAllAplusMulA, stream(1, 2, 4, 16), enc(), false },
            { "[1, 2, 3, 1, 2, 3] a, a, a, catAll(a)", reduceCatA, stream(1, 2, 3, 1, 2, 3), enc(), true },
            { "[1, 2, 3, 3, 2, 1] a, a, a, catAll(a)", reduceCatA, stream(1, 2, 3, 3, 2, 1), enc(), false },
            { "[1, 2, 3, 1, 2, 3] a, a, a, catAll(a) BE", reduceCatAToNumDefBEPostBE, stream(1, 2, 3, 1, 2, 3), enc(), true },
            { "[1, 2, 3, 3, 2, 1] a, a, a, catAll(a) BE", reduceCatAToNumDefBEPostBE, stream(1, 2, 3, 3, 2, 1), enc(), false },
            { "[1, 2, 3, 1, 2, 3] a, a, a, catAll(a) BE", reduceCatAToNumDefBEPostLE, stream(1, 2, 3, 3, 2, 1), enc(), true },
            { "[1, 2, 3, 3, 2, 1] a, a, a, catAll(a) BE", reduceCatAToNumDefBEPostLE, stream(1, 2, 3, 1, 2, 3), enc(), false },
            { "[3, 2, 1, 3, 2, 1] a, a, a, catAll(a) LE", reduceCatAToNumDefLEPostBE, stream(3, 2, 1, 1, 2, 3), enc(), true },
            { "[3, 2, 1, 1, 2, 3] a, a, a, catAll(a) LE", reduceCatAToNumDefLEPostBE, stream(3, 2, 1, 3, 2, 1), enc(), false },
            { "[3, 2, 1, 3, 2, 1] a, a, a, catAll(a) LE", reduceCatAToNumDefLEPostLE, stream(3, 2, 1, 3, 2, 1), enc(), true },
            { "[3, 2, 1, 1, 2, 3] a, a, a, catAll(a) LE", reduceCatAToNumDefLEPostLE, stream(3, 2, 1, 1, 2, 3), enc(), false },
            { "[10, 3, 2, 5] a, a, a, subAll(a) left", foldLeftSubA, stream(10, 3, 2, 5), enc(), true },
            { "[10, 3, 2, -13] a, a, a, subAll(a, 2) left", foldLeftSubAInit2, stream(10, 3, 2, -13), enc(), true },
            { "[10, 3, 2, 9] a, a, a, subAll(a) right", foldRightSubA, stream(10, 3, 2, 9), enc(), true },
            { "[10, 3, 2, 7] a, a, a, subAll(a, 2) right", foldRightSubAInit2, stream(10, 3, 2, 7), enc(), true }
        });
    }

}
