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

package nl.minvenj.nfi.ddrx;

import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.eqNum;
import static nl.minvenj.nfi.ddrx.Shorthand.eqStr;
import static nl.minvenj.nfi.ddrx.Shorthand.expTrue;
import static nl.minvenj.nfi.ddrx.Shorthand.gtNum;
import static nl.minvenj.nfi.ddrx.Shorthand.ltNum;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.self;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.comparison.ComparisonExpression;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class ComparisonExpressionSemantics extends ParameterizedParse {

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
            { "0x01 == 0x01", valCom(1, eq(ref("a"))), stream(1, 1), enc(), true },
            { "0x02 == 0x01", valCom(1, eq(ref("a"))), stream(1, 2), enc(), false },
            { "0x01 == 0x01 with self", valCom(1, eq(self, ref("a"))), stream(1, 1), enc(), true },
            { "0x01 == 0x02 with self", valCom(1, eq(self, ref("a"))), stream(1, 2), enc(), false }
        });
    }

    public ComparisonExpressionSemantics(String desc, Token token, Environment env, Encoding enc, boolean result) {
        super(token, env, enc, result);
    }

    private static Token numCom(int size, ComparisonExpression comparison) {
        return seq(any("a"),
                   def("b", con(size), comparison));
    }

    private static Token strCom(int size, ComparisonExpression comparison) {
        return seq(def("a", con(size), expTrue()),
                   def("b", con(size), comparison));
    }

    private static Token valCom(int size, ComparisonExpression comparison) {
        return seq(def("a", con(size), expTrue()),
                   def("b", con(size), comparison));
    }

}
