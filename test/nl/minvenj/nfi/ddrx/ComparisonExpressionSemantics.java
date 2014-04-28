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
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.data.Environment.stream;

import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.expression.comparison.ComparisonExpression;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.runners.Parameterized.Parameters;

public class ComparisonExpressionSemantics extends ParameterizedParse {

    @Parameters(name="{0} ({3})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "1 == 1", numCom(1, eqNum(ref("a"))), stream(1, 1), true },
            { "2 == 1", numCom(1, eqNum(ref("a"))), stream(1, 2), false },
            { "1 > 1", numCom(1, gtNum(ref("a"))), stream(1, 1), false},
            { "2 > 1", numCom(1, gtNum(ref("a"))), stream(1, 2), true},
            { "1 > 2", numCom(1, gtNum(ref("a"))), stream(2, 1), false},
            { "1 < 1", numCom(1, ltNum(ref("a"))), stream(1, 1), false},
            { "2 < 1", numCom(1, ltNum(ref("a"))), stream(1, 2), false},
            { "1 < 2", numCom(1, ltNum(ref("a"))), stream(2, 1), true},
            { "\"abc\" == \"abc\"", strCom(3, eqStr(ref("a"))), stream("abcabc"), true},
            { "\"abd\" == \"abc\"", strCom(3, eqStr(ref("a"))), stream("abcabd"), false},
            { "0x01 == 0x01", valCom(1, eq(ref("a"))), stream(1, 1), true},
            { "0x02 == 0x01", valCom(1, eq(ref("a"))), stream(1, 2), false}
        });
    }

    public ComparisonExpressionSemantics(String desc, Token token, Environment env, boolean result) {
        super(token, env, result);
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
