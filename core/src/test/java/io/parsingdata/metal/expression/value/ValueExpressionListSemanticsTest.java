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

package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.first;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.neg;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class ValueExpressionListSemanticsTest extends ParameterizedParse {

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "a, a, b, b, last(a+b)", pred2(eq(last(add(ref("a"), ref("b"))))), stream(1, 2, 3, 4, 6), enc(), true },
            { "a, a, b, b, first(a+b)", pred2(eq(first(add(ref("a"), ref("b"))))), stream(1, 2, 3, 4, 4), enc(), true },
            { "a, a, a, b, b, last(a+b)", pred3(eq(last(add(ref("a"), ref("b"))))), stream(1, 2, 3, 4, 5, 8), enc(), true },
            { "a, a, a, b, b, first(a+b)", pred3(eq(first(add(ref("a"), ref("b"))))), stream(1, 2, 3, 4, 5, 5), enc(), false },
            { "a, a, b, b, last(offset(a)+offset(b))", pred2(eq(last(add(offset(ref("a")), offset(ref("b")))))), stream(1, 2, 3, 4, 4), enc(), true },
            { "a, a, b, b, first(offset(a)+offset(b))", pred2(eq(first(add(offset(ref("a")), offset(ref("b")))))), stream(1, 2, 3, 4, 2), enc(), true },
            { "a, a, a, b, b, last((offset(a)+offset(b))", pred3(eq(last(add(offset(ref("a")), offset(ref("b")))))), stream(1, 2, 3, 4, 5, 6), enc(), true },
            { "a, a, a, b, b, first(offset(a)+offset(b))", pred3(eq(first(add(offset(ref("a")), offset(ref("b")))))), stream(1, 2, 3, 4, 5, 3), enc(), false },
            { "a, a, a, b, b, last(a)+first(b)", pred3(eq(add(last(ref("a")), first(ref("b"))))), stream(1, 2, 3, 4, 5, 7), enc(), true },
            { "a, a, a, b, b, first(a)+last(b)", pred3(eq(add(first(ref("a")), last(ref("b"))))), stream(1, 2, 3, 4, 5, 6), enc(), true },
            { "a, a, b, b, last(not(a)+not(b))", pred2(eq(last(add(not(ref("a")), not(ref("b")))))), stream(1, 240, 3, 15, -1), enc(), true },
            { "a, a, b, b, first(not(a)+not(b))", pred2(eq(first(add(not(ref("a")), not(ref("b")))))), stream(230, 2, 25, 4, -1), enc(), true },
            { "a, a, a, b, b, last(not(a)+not(b))", pred3(eq(last(add(not(ref("a")), not(ref("b")))))), stream(1, 2, 200, 4, 55, -1), enc(), true },
            { "a, a, a, b, b, first(not(a)+not(b))", pred3(eq(first(add(not(ref("a")), not(ref("b")))))), stream(200, 2, 3, 55, 5, -1), enc(), false },
            { "a, a, last(neg(a))", pred(eq(last(neg(ref("a"))))), stream(1, 2, -2), signed(), true },
            { "a, a, first(neg(a))", pred(eq(first(neg(ref("a"))))), stream(1, 2, -1), signed(), true }

        });
    }

    private static Token pred(Expression predicate) {
        return
            seq(any("a"),
                any("a"),
                def("b", con(1), predicate));
    }

    private static Token pred2(Expression predicate) {
        return
            seq(any("a"),
                any("a"),
                any("b"),
                any("b"),
                def("c", con(1), predicate));
    }

    private static Token pred3(Expression predicate) {
        return
            seq(any("a"),
                any("a"),
                any("a"),
                any("b"),
                any("b"),
                def("c", con(1), predicate));
    }

}
