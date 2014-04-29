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

import static nl.minvenj.nfi.ddrx.Shorthand.add;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.div;
import static nl.minvenj.nfi.ddrx.Shorthand.eqNum;
import static nl.minvenj.nfi.ddrx.Shorthand.mul;
import static nl.minvenj.nfi.ddrx.Shorthand.neg;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.sub;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.value.BinaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.UnaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class NumericValueExpressionSemantics extends ParameterizedParse {

    @Parameters(name="{0} ({3})")
    public static Collection<Object[]> data() {
        Encoding se = new Encoding(true);
        return Arrays.asList(new Object[][] {
            { "[signed] 1 + 2 == 3", addSigned, stream(se, 1, 2, 3), true },
            { "[signed] -10 + 3 == -7", addSigned, stream(se, -10, 3, -7), true },
            { "[signed] -10 + -8 == -18", addSigned, stream(se, -10, -8, -18), true },
            { "[signed] 10 + -7 == 3", addSigned, stream(se, 10, -7, 3), true },
            { "[signed] 10 + -25 == -15", addSigned, stream(se, 10, -25, -15), true },
            { "[signed] 1 + 2 == 4", addSigned, stream(se, 1, 2, 4), false },
            { "[unsigned] 1 + 2 == 3", addUnsigned, stream(1, 2, 3), true },
            { "[unsigned] -10 + 3 == -7", addUnsigned, stream(-10, 3, -7), true },
            { "[unsigned] 1 + 2 == 4", addUnsigned, stream(1, 2, 4), false },
            { "[unsigned] 130 + 50 == 180", addUnsigned, stream(130, 50, 180), true },
            { "[unsigned] 130 + 50 == 180", addUnsigned, stream(130, 50, 180), true },
            { "[signed] 8 / 2 == 4", div, stream(se, 8, 2, 4), true },
            { "[signed] 1 / 2 == 0", div, stream(se, 1, 2, 0), true },
            { "[signed] 7 / 8 == 0", div, stream(se, 7, 8, 0), true },
            { "[signed] 3 / 2 == 1", div, stream(se, 3, 2, 1), true },
            { "[signed] 1 / 1 == 1", div, stream(se, 1, 1, 1), true },
            { "[signed] 4 / 2 == 1", div, stream(se, 4, 2, 1), false },
            { "[signed] 2 * 2 == 4", mul, stream(se, 2, 2, 4), true },
            { "[signed] 0 * 42 == 0", mul, stream(se, 0, 42, 0), true },
            { "[signed] 42 * 0 == 0", mul, stream(se, 42, 0, 0), true },
            { "[signed] 1 * 1 == 1", mul, stream(se, 1, 1, 1), true },
            { "[signed] 0 * 0 == 0", mul, stream(se, 0, 0, 0), true },
            { "[signed] 2 * 3 == 8", mul, stream(se, 2, 3, 8), false },
            { "[signed] 8 - 2 == 6", sub, stream(se, 8, 2, 6), true },
            { "[signed] 3 - 10 == -7", sub, stream(se, 3, 10, -7), true },
            { "[signed] 0 - 42 == -42", sub, stream(se, 0, 42, -42), true },
            { "[signed] -42 - 10 == -52", sub, stream(se, -42, 10, -52), true },
            { "[signed] -42 - -10 == -32", sub, stream(se, -42, -10, -32), true },
            { "[signed] -42 - 42 == 0", sub, stream(se, -42, 42, 0), false },
            { "[signed] -(1) == -1", neg, stream(se, 1, -1), true },
            { "[signed] -(2) == -2", neg, stream(se, 2, -2), true },
            { "[signed] -(3) == -3", neg, stream(se, 3, -3), true },
            { "[signed] -(0) == 0", neg, stream(se, 0, 0), true },
            { "[signed] -(4) == 4", neg, stream(se, 4, 4), false },
            { "[signed] -(-5) == -5", neg, stream(se, -5, -5), false }
        });
    }

    public NumericValueExpressionSemantics(String desc, Token token, Environment env, boolean result) {
        super(token, env, result);
    }

    private static Token addSigned = binaryValueExpressionToken(add(ref("a"), ref("b")), new Encoding(true));
    private static Token addUnsigned = binaryValueExpressionToken(add(ref("a"), ref("b")), new Encoding(false));
    private static Token div = binaryValueExpressionToken(div(ref("a"), ref("b")), new Encoding(true));
    private static Token mul = binaryValueExpressionToken(mul(ref("a"), ref("b")), new Encoding(true));
    private static Token sub = binaryValueExpressionToken(sub(ref("a"), ref("b")), new Encoding(true));
    private static Token neg = unaryValueExpressionToken(neg(ref("a")), new Encoding(true));

    private static Token singleToken(String firstName, String secondName, ValueExpression ve, Encoding encoding) {
        return seq(any(firstName, encoding),
                   def(secondName,
                          con(1),
                          eqNum(ve),
                          encoding));
    }

    private static Token binaryValueExpressionToken(BinaryValueExpression bve, Encoding encoding) {
        return seq(any("a", encoding),
                   singleToken("b", "c", bve, encoding));
    }

    private static Token unaryValueExpressionToken(UnaryValueExpression uve, Encoding encoding) {
        return singleToken("a", "b", uve, encoding);
    }

}
