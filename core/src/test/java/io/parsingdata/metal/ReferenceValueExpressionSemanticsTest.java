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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.first;
import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.join;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static io.parsingdata.metal.util.TokenDefinitions.eqRef;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.reference.Ref;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class ReferenceValueExpressionSemanticsTest extends ParameterizedParse {

    private static final Token sequenceMatch2 = seq(any("a"),
        eqRef("b", "a"));
    private static final Token sequenceMatch3 = seq(sequenceMatch2,
        eqRef("c", "a"));
    private static final Token sequenceMatchTransitive3 = seq(sequenceMatch2,
        eqRef("c", "b"));

    private static Token refList(final String first, final String second, final ValueExpression exp) {
        return seq(any(first),
            any(second),
            def("z", con(1), eq(exp)));
    }

    private static final Token refAny = any("a");

    private static Token refMatch(final Expression pred) {
        return
            seq(repn(refAny, con(3)),
                def("b", con(1), pred));
    }

    private static <T> Token limitedSum(final Ref<T> ref) {
        return
            seq(rep(def("a", con(1), not(eq(con(0))))),
                def("zero", con(1), eq(con(0))),
                def("sum", con(1), eq(fold(join(con(0), ref), Shorthand::add))));
    }

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "[2, 2] b == a", sequenceMatch2, stream(2, 2), enc(), true },
            { "[2, 1] b == a", sequenceMatch2, stream(2, 1), enc(), false },
            { "[2, 2] b == a, c == a", sequenceMatch3, stream(2, 2, 2), enc(), true },
            { "[2, 2, 1] b == a, c == a", sequenceMatch3, stream(2, 2, 1), enc(), false },
            { "[2, 1, 2] b == a, c == a", sequenceMatch3, stream(2, 1, 2), enc(), false },
            { "[1, 2, 2] b == a, c == a", sequenceMatch3, stream(1, 2, 2), enc(), false },
            { "[1, 2, 3] b == a, c == a", sequenceMatch3, stream(1, 2, 3), enc(), false },
            { "[2, 2, 2] b == a, c == b", sequenceMatchTransitive3, stream(2, 2, 2), enc(), true },
            { "[2, 2, 1] b == a, c == b", sequenceMatchTransitive3, stream(2, 2, 1), enc(), false },
            { "[2, 1, 2] b == a, c == b", sequenceMatchTransitive3, stream(2, 1, 2), enc(), false },
            { "[1, 2, 2] b == a, c == b", sequenceMatchTransitive3, stream(1, 2, 2), enc(), false },
            { "[1, 2, 3] b == a, c == b", sequenceMatchTransitive3, stream(1, 2, 3), enc(), false },
            { "[1, 2, 1] a, a, first(a)", refList("a", "a", first(ref("a"))), stream(1, 2, 1), enc(), true },
            { "[1, 2, 3] a, a, first(a)", refList("a", "a", first(ref("a"))), stream(1, 2, 3), enc(), false },
            { "[1, 2, 3] a, a, first(b)", refList("a", "a", first(ref("b"))), stream(1, 2, 3), enc(), false },
            { "[1, 2, 3] a, a, ref(b)", refList("a", "a", ref("b")), stream(1, 2, 3), enc(), false },
            { "[1, 2, 2] a, a, last(a)", refList("a", "a", last(ref("a"))), stream(1, 2, 2), enc(), true },
            { "[1, 2, 1] a, a, last(a)", refList("a", "a", last(ref("a"))), stream(1, 2, 1), enc(), false },
            { "[1, 2, 0] a, b, offset(last(a))", refList("a", "b", offset(last(ref("a")))), stream(1, 2, 0), enc(), true },
            { "[1, 2, 1] a, a, offset(last(a))", refList("a", "a", offset(last(ref("a")))), stream(1, 2, 1), enc(), true },
            { "[1, 2, 2] a, b, offset(last(z))", refList("a", "b", offset(last(ref("z")))), stream(1, 2, 2), enc(), true },
            { "[1, 2, 3] a, b, offset(last(c))", refList("a", "b", offset(last(ref("c")))), stream(1, 2, 3), enc(), false },
            { "[1, 2, 0] a, b, offset(first(a))", refList("a", "b", offset(first(ref("a")))), stream(1, 2, 0), enc(), true },
            { "[1, 2, 1] a, a, offset(first(a))", refList("a", "a", offset(first(ref("a")))), stream(1, 2, 1), enc(), false },
            { "[2, 1, 0] a, a, offset(first(a))", refList("a", "a", offset(first(ref("a")))), stream(2, 1, 0), enc(), true },
            { "[1, 2, 2] a, b, offset(first(z))", refList("a", "b", offset(first(ref("z")))), stream(1, 2, 2), enc(), true },
            { "[1, 2, 3] a, b, offset(first(c))", refList("a", "b", offset(first(ref("c")))), stream(1, 2, 3), enc(), false },
            { "[1, 2, 3, 3] a, a, a, last(ref(a.definition))", refMatch(eq(last(ref(refAny)))), stream(1, 2, 3, 3), enc(), true },
            { "[1, 2, 3, 1] a, a, a, first(ref(a.definition))", refMatch(eq(first(ref(refAny)))), stream(1, 2, 3, 1), enc(), true },
            { "[1, 2, 3, 6] a, a, a, first(fold(ref(a.definition), add))", refMatch(eq(first(fold(ref(refAny), Shorthand::add)))), stream(1, 2, 3, 6), enc(), true },
            { "[1, 2, 3, 2] a, a, a, last(ref(a.definition))", refMatch(eq(last(ref(refAny)))), stream(1, 2, 3, 2), enc(), false },
            { "[1, 2, 3, 0, 0] a, a, a, 0, sum(ref(a, 0))", limitedSum(ref("a", con(0))), stream(1, 2, 3, 0, 0), enc(), true },
            { "[1, 2, 3, 0, 3] a, a, a, 0, sum(ref(a, 1))", limitedSum(ref("a", con(1))), stream(1, 2, 3, 0, 3), enc(), true },
            { "[1, 2, 3, 0, 5] a, a, a, 0, sum(ref(a, 2))", limitedSum(ref("a", con(2))), stream(1, 2, 3, 0, 5), enc(), true },
            { "[1, 2, 3, 0, 6] a, a, a, 0, sum(ref(a, 3))", limitedSum(ref("a", con(3))), stream(1, 2, 3, 0, 6), enc(), true },
            { "[1, 2, 3, 0, 6] a, a, a, 0, sum(ref(a, 4))", limitedSum(ref("a", con(4))), stream(1, 2, 3, 0, 6), enc(), true },
            { "[1, 2, 3, 0, 0] a, a, a, 0, sum(ref(0, a))", limitedSum(ref(con(0), "a")), stream(1, 2, 3, 0, 0), enc(), true },
            { "[1, 2, 3, 0, 3] a, a, a, 0, sum(ref(1, a))", limitedSum(ref(con(1), "a")), stream(1, 2, 3, 0, 3), enc(), true },
            { "[1, 2, 3, 0, 5] a, a, a, 0, sum(ref(2, a))", limitedSum(ref(con(2), "a")), stream(1, 2, 3, 0, 5), enc(), true },
            { "[1, 2, 3, 0, 6] a, a, a, 0, sum(ref(3, a))", limitedSum(ref(con(3), "a")), stream(1, 2, 3, 0, 6), enc(), true },
            { "[1, 2, 3, 0, 6] a, a, a, 0, sum(ref(4, a))", limitedSum(ref(con(4), "a")), stream(1, 2, 3, 0, 6), enc(), true },
            { "[1, 2, 3, 0, 0] a, a, a, 0, sum(ref(0, a.definition))", limitedSum(ref(con(0), any("a"))), stream(1, 2, 3, 0, 0), enc(), true },
            { "[1, 2, 3, 0, 3] a, a, a, 0, sum(ref(1, a.definition))", limitedSum(ref(con(1), any("a"))), stream(1, 2, 3, 0, 3), enc(), true },
            { "[1, 2, 3, 0, 5] a, a, a, 0, sum(ref(2, a.definition))", limitedSum(ref(con(2), any("a"))), stream(1, 2, 3, 0, 5), enc(), true },
            { "[1, 2, 3, 0, 6] a, a, a, 0, sum(ref(3, a.definition))", limitedSum(ref(con(3), any("a"))), stream(1, 2, 3, 0, 6), enc(), true },
            { "[1, 2, 3, 0, 6] a, a, a, 0, sum(ref(4, a.definition))", limitedSum(ref(con(4), any("a"))), stream(1, 2, 3, 0, 6), enc(), true },
            { "[1, 2, 3, 0, 0] a, a, a, 0, sum(ref(a.definition, 0))", limitedSum(ref(any("a"), con(0))), stream(1, 2, 3, 0, 0), enc(), true },
            { "[1, 2, 3, 0, 3] a, a, a, 0, sum(ref(a.definition, 1))", limitedSum(ref(any("a"), con(1))), stream(1, 2, 3, 0, 3), enc(), true },
            { "[1, 2, 3, 0, 5] a, a, a, 0, sum(ref(a.definition, 2))", limitedSum(ref(any("a"), con(2))), stream(1, 2, 3, 0, 5), enc(), true },
            { "[1, 2, 3, 0, 6] a, a, a, 0, sum(ref(a.definition, 3))", limitedSum(ref(any("a"), con(3))), stream(1, 2, 3, 0, 6), enc(), true },
            { "[1, 2, 3, 0, 6] a, a, a, 0, sum(ref(a.definition, 4))", limitedSum(ref(any("a"), con(4))), stream(1, 2, 3, 0, 6), enc(), true }
        });
    }

}
