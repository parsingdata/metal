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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.eqNum;
import static nl.minvenj.nfi.metal.Shorthand.eqStr;
import static nl.minvenj.nfi.metal.Shorthand.expTrue;
import static nl.minvenj.nfi.metal.Shorthand.gtNum;
import static nl.minvenj.nfi.metal.Shorthand.ltNum;
import static nl.minvenj.nfi.metal.Shorthand.ref;
import static nl.minvenj.nfi.metal.Shorthand.self;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.TokenDefinitions.any;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.comparison.ComparisonExpression;
import nl.minvenj.nfi.metal.token.Token;
import nl.minvenj.nfi.metal.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class ComparisonExpressionSemanticsTest extends ParameterizedParse {

    @Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "1 == 1", numCom(1, eqNum(ref("a"))), stream(1, 1), enc(), true },
            { "2 == 1", numCom(1, eqNum(ref("a"))), stream(1, 2), enc(), false },
            { "1 > 1", numCom(1, gtNum(ref("a"))), stream(1, 1), enc(), false },
            { "2 > 1", numCom(1, gtNum(ref("a"))), stream(1, 2), enc(), true },
            { "1 > 2", numCom(1, gtNum(ref("a"))), stream(2, 1), enc(), false },
            { "1 < 1", numCom(1, ltNum(ref("a"))), stream(1, 1), enc(), false },
            { "2 < 1", numCom(1, ltNum(ref("a"))), stream(1, 2), enc(), false },
            { "1 < 2", numCom(1, ltNum(ref("a"))), stream(2, 1), enc(), true },
            { "\"abc\" == \"abc\"", strCom(3, eqStr(ref("a"))), stream("abcabc", Charset.forName("ISO646-US")), enc(), true },
            { "\"abd\" == \"abc\"", strCom(3, eqStr(ref("a"))), stream("abcabd", Charset.forName("ISO646-US")), enc(), false },
            { "1 == 1", valCom(1, eq(ref("a"))), stream(1, 1), enc(), true },
            { "2 == 1", valCom(1, eq(ref("a"))), stream(1, 2), enc(), false },
            { "1 == 1 with self", valCom(1, eq(self, ref("a"))), stream(1, 1), enc(), true },
            { "1 == 2 with self", valCom(1, eq(self, ref("a"))), stream(1, 2), enc(), false }
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

}
