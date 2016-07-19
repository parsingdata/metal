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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.eqStr;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.self;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

import io.parsingdata.metal.expression.Expression;
import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.comparison.ComparisonExpression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class ComparisonExpressionSemanticsTest extends ParameterizedParse {

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
            { "\"abc\" == \"abc\"", strCom(3, eqStr(ref("a"))), stream("abcabc", Charset.forName("ISO646-US")), enc(), true },
            { "\"abd\" == \"abc\"", strCom(3, eqStr(ref("a"))), stream("abcabd", Charset.forName("ISO646-US")), enc(), false },
            { "1 == 1(eq)", valCom(1, eq(ref("a"))), stream(1, 1), enc(), true },
            { "2 == 1(eq)", valCom(1, eq(ref("a"))), stream(1, 2), enc(), false },
            { "1 == 1 with self", valCom(1, eq(self, ref("a"))), stream(1, 1), enc(), true },
            { "1 == 2 with self", valCom(1, eq(self, ref("a"))), stream(1, 2), enc(), false },
            { "1, 2 == 1", listCom(eq(ref("a"), ref("b")), expTrue()), stream(1, 2, 1, 2), enc(), false },
            { "1, 2 == 1, 2", listCom(expTrue(), eq(ref("a"), ref("b"))), stream(1, 2, 1, 2), enc(), true },
            { "1, 2 == 2, 2", listCom(expTrue(), eq(ref("a"), ref("b"))), stream(1, 2, 2, 2), enc(), false },
            { "1, 2 == 1, 3", listCom(expTrue(), eq(ref("a"), ref("b"))), stream(1, 2, 1, 3), enc(), false }
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
