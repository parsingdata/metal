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

import static java.nio.charset.StandardCharsets.US_ASCII;

import static io.parsingdata.metal.Shorthand.SELF;
import static io.parsingdata.metal.Shorthand.TRUE;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.eqStr;
import static io.parsingdata.metal.Shorthand.gtEqNum;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.ltEqNum;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.ComparisonExpression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class ComparisonExpressionSemanticsTest extends ParameterizedParse {

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "1 == 1(eqNum)", numericCompare(1, eqNum(ref("a"))), stream(1, 1), enc(), true },
            { "2 == 1(eqNum)", numericCompare(1, eqNum(ref("a"))), stream(1, 2), enc(), false },
            { "1 >= 1", numericCompare(1, gtEqNum(ref("a"))), stream(1, 1), enc(), true },
            { "2 >= 1", numericCompare(1, gtEqNum(ref("a"))), stream(1, 2), enc(), true },
            { "1 >= 2", numericCompare(1, gtEqNum(ref("a"))), stream(2, 1), enc(), false },
            { "1 > 1", numericCompare(1, gtNum(ref("a"))), stream(1, 1), enc(), false },
            { "2 > 1", numericCompare(1, gtNum(ref("a"))), stream(1, 2), enc(), true },
            { "1 > 2", numericCompare(1, gtNum(ref("a"))), stream(2, 1), enc(), false },
            { "1 <= 1", numericCompare(1, ltEqNum(ref("a"))), stream(1, 1), enc(), true },
            { "2 <= 1", numericCompare(1, ltEqNum(ref("a"))), stream(1, 2), enc(), false },
            { "1 <= 2", numericCompare(1, ltEqNum(ref("a"))), stream(2, 1), enc(), true },
            { "1 < 1", numericCompare(1, ltNum(ref("a"))), stream(1, 1), enc(), false },
            { "2 < 1", numericCompare(1, ltNum(ref("a"))), stream(1, 2), enc(), false },
            { "1 < 2", numericCompare(1, ltNum(ref("a"))), stream(2, 1), enc(), true },
            { "\"abc\" == \"abc\"", stringCompare(3, eqStr(ref("a"))), stream("abcabc", US_ASCII), new Encoding(US_ASCII), true },
            { "\"abd\" == \"abc\"", stringCompare(3, eqStr(ref("a"))), stream("abcabd", US_ASCII), new Encoding(US_ASCII), false },
            { "1 == 1(eq)", valueCompare(1, eq(ref("a"))), stream(1, 1), enc(), true },
            { "2 == 1(eq)", valueCompare(1, eq(ref("a"))), stream(1, 2), enc(), false },
            { "1 == 1 with self", valueCompare(1, eq(SELF, ref("a"))), stream(1, 1), enc(), true },
            { "1 == 2 with self", valueCompare(1, eq(SELF, ref("a"))), stream(1, 2), enc(), false },
            { "1, 2 == 1", listCompare(eq(ref("a"), ref("b")), TRUE), stream(1, 2, 1, 2), enc(), false },
            { "1, 2 == 1, 2", listCompare(TRUE, eq(ref("a"), ref("b"))), stream(1, 2, 1, 2), enc(), true },
            { "1, 2 == 2, 2", listCompare(TRUE, eq(ref("a"), ref("b"))), stream(1, 2, 2, 2), enc(), false },
            { "1, 2 == 1, 3", listCompare(TRUE, eq(ref("a"), ref("b"))), stream(1, 2, 1, 3), enc(), false },
            { "1, 2, 1 != 1/0", valueCompare(1, eqNum(con(1), div(con(1), con(0)))), stream(1, 2), enc(), false },
            { "1, 2, 1/0 != 1", valueCompare(1, eqNum(div(con(1), con(0)), con(1))), stream(1, 2), enc(), false }
        });
    }

    private static Token numericCompare(final int size, final ComparisonExpression comparison) {
        return seq(any("a"),
                   def("b", con(size), comparison));
    }

    private static Token stringCompare(final int size, final ComparisonExpression comparison) {
        return seq(def("a", con(size), TRUE),
                   def("b", con(size), comparison));
    }

    private static Token valueCompare(final int size, final ComparisonExpression comparison) {
        return seq(def("a", con(size), TRUE),
                   def("b", con(size), comparison));
    }

    private static Token listCompare(final Expression first, final Expression second) {
        return seq(any("a"),
                   any("a"),
                   def("b", con(1), first),
                   def("b", con(1), second));
    }
}
