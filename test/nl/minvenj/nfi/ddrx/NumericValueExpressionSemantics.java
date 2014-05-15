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
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.*;

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
        return Arrays.asList(new Object[][] {
            { "[signed] 1 + 2 == 3", add, stream(1, 2, 3), signed(), true },
            { "[signed] -10 + 3 == -7", add, stream(-10, 3, -7), signed(), true },
            { "[signed] -10 + -8 == -18", add, stream(-10, -8, -18), signed(), true },
            { "[signed] 10 + -7 == 3", add, stream(10, -7, 3), signed(), true },
            { "[signed] 10 + -25 == -15", add, stream(10, -25, -15), signed(), true },
            { "[signed] 1 + 2 == 4", add, stream(1, 2, 4), signed(), false },
            { "[unsigned] 1 + 2 == 3", add, stream(1, 2, 3), enc(), true },
            { "[unsigned] -10 + 3 == -7", add, stream(-10, 3, -7), enc(), true },
            { "[unsigned] 1 + 2 == 4", add, stream(1, 2, 4), enc(), false },
            { "[unsigned] 130 + 50 == 180", add, stream(130, 50, 180), enc(), true },
            { "[unsigned] 130 + 50 == 180", add, stream(130, 50, 180), enc(), true },
            { "[signed] 8 / 2 == 4", div, stream(8, 2, 4), signed(), true },
            { "[signed] 1 / 2 == 0", div, stream(1, 2, 0), signed(), true },
            { "[signed] 7 / 8 == 0", div, stream(7, 8, 0), signed(), true },
            { "[signed] 3 / 2 == 1", div, stream(3, 2, 1), signed(), true },
            { "[signed] 1 / 1 == 1", div, stream(1, 1, 1), signed(), true },
            { "[signed] 4 / 2 == 1", div, stream(4, 2, 1), signed(), false },
            { "[signed] 2 * 2 == 4", mul, stream(2, 2, 4), signed(), true },
            { "[signed] 0 * 42 == 0", mul, stream(0, 42, 0), signed(), true },
            { "[signed] 42 * 0 == 0", mul, stream(42, 0, 0), signed(), true },
            { "[signed] 1 * 1 == 1", mul, stream(1, 1, 1), signed(), true },
            { "[signed] 0 * 0 == 0", mul, stream(0, 0, 0), signed(), true },
            { "[signed] 2 * 3 == 8", mul, stream(2, 3, 8), signed(), false },
            { "[signed] 8 - 2 == 6", sub, stream(8, 2, 6), signed(), true },
            { "[signed] 3 - 10 == -7", sub, stream(3, 10, -7), signed(), true },
            { "[signed] 0 - 42 == -42", sub, stream(0, 42, -42), signed(), true },
            { "[signed] -42 - 10 == -52", sub, stream(-42, 10, -52), signed(), true },
            { "[signed] -42 - -10 == -32", sub, stream(-42, -10, -32), signed(), true },
            { "[signed] -42 - 42 == 0", sub, stream(-42, 42, 0), signed(), false },
            { "[signed] -(1) == -1", neg, stream(1, -1), signed(), true },
            { "[signed] -(2) == -2", neg, stream(2, -2), signed(), true },
            { "[signed] -(3) == -3", neg, stream(3, -3), signed(), true },
            { "[signed] -(0) == 0", neg, stream(0, 0), signed(), true },
            { "[signed] -(4) == 4", neg, stream(4, 4), signed(), false },
            { "[signed] -(-5) == -5", neg, stream(-5, -5), signed(), false }
        });
    }

    public NumericValueExpressionSemantics(String desc, Token token, Environment env, Encoding enc, boolean result) {
        super(token, env, enc, result);
    }

    private static Token add = binaryValueExpressionToken(add(ref("a"), ref("b")));
    private static Token div = binaryValueExpressionToken(div(ref("a"), ref("b")));
    private static Token mul = binaryValueExpressionToken(mul(ref("a"), ref("b")));
    private static Token sub = binaryValueExpressionToken(sub(ref("a"), ref("b")));
    private static Token neg = unaryValueExpressionToken(neg(ref("a")));

    private static Token singleToken(String firstName, String secondName, ValueExpression ve) {
        return seq(any(firstName),
                   def(secondName,
                          con(1),
                          eqNum(ve)));
    }

    private static Token binaryValueExpressionToken(BinaryValueExpression bve) {
        return seq(any("a"),
                   singleToken("b", "c", bve));
    }

    private static Token unaryValueExpressionToken(UnaryValueExpression uve) {
        return singleToken("a", "b", uve);
    }

}
