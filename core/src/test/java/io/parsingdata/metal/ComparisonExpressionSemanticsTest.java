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

package io.parsingdata.metal;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.ComparisonExpression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;
import org.junit.runners.Parameterized.Parameters;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

public class ComparisonExpressionSemanticsTest extends ParameterizedParse {

    public static final Charset ASCII = Charset.forName("ISO646-US");

    @Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "1 == 1(eqNum)", numCom(1, eqNum(ref("a"))), stream(1, 1), enc(), true },
            { "2 == 1(eqNum)", numCom(1, eqNum(ref("a"))), stream(1, 2), enc(), false },
            { "1 > 1", numCom(1, gtNum(ref("a"))), stream(1, 1), enc(), false },
            { "2 > 1", numCom(1, gtNum(ref("a"))), stream(1, 2), enc(), true },
            { "1 > 2", numCom(1, gtNum(ref("a"))), stream(2, 1), enc(), false },
            { "1 < 1", numCom(1, ltNum(ref("a"))), stream(1, 1), enc(), false },
            { "2 < 1", numCom(1, ltNum(ref("a"))), stream(1, 2), enc(), false },
            { "1 < 2", numCom(1, ltNum(ref("a"))), stream(2, 1), enc(), true },
            { "\"abc\" == \"abc\"", strCom(3, eqStr(ref("a"))), stream("abcabc", ASCII), new Encoding(ASCII), true },
            { "\"abd\" == \"abc\"", strCom(3, eqStr(ref("a"))), stream("abcabd", ASCII), new Encoding(ASCII), false },
            { "1 == 1(eq)", valCom(1, eq(ref("a"))), stream(1, 1), enc(), true },
            { "2 == 1(eq)", valCom(1, eq(ref("a"))), stream(1, 2), enc(), false },
            { "1 == 1 with self", valCom(1, eq(self, ref("a"))), stream(1, 1), enc(), true },
            { "1 == 2 with self", valCom(1, eq(self, ref("a"))), stream(1, 2), enc(), false },
            { "1, 2 == 1", listCom(eq(ref("a"), ref("b")), expTrue()), stream(1, 2, 1, 2), enc(), false },
            { "1, 2 == 1, 2", listCom(expTrue(), eq(ref("a"), ref("b"))), stream(1, 2, 1, 2), enc(), true },
            { "1, 2 == 2, 2", listCom(expTrue(), eq(ref("a"), ref("b"))), stream(1, 2, 2, 2), enc(), false },
            { "1, 2 == 1, 3", listCom(expTrue(), eq(ref("a"), ref("b"))), stream(1, 2, 1, 3), enc(), false },
            { "1, 2, 1 != 1/0", valCom(1, eqNum(con(1), div(con(1), con(0)))), stream(1, 2), enc(), false },
            { "1, 2, 1/0 != 1", valCom(1, eqNum(div(con(1), con(0)), con(1))), stream(1, 2), enc(), false }
        });
    }

    public ComparisonExpressionSemanticsTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

    private static Token numCom(final int size, final ComparisonExpression comparison) {
        return seq(any("a"),
                   def("b", con(size), comparison));
    }

    private static Token strCom(final int size, final ComparisonExpression comparison) {
        return seq(def("a", con(size), expTrue()),
                   def("b", con(size), comparison));
    }

    private static Token valCom(final int size, final ComparisonExpression comparison) {
        return seq(def("a", con(size), expTrue()),
                   def("b", con(size), comparison));
    }

    private static Token listCom(final Expression first, final Expression second) {
        return seq(any("a"),
                   any("a"),
                   def("b", con(1), first),
                   def("b", con(1), second));
    }
}
