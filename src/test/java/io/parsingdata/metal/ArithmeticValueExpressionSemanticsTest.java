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

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.mul;
import static io.parsingdata.metal.Shorthand.neg;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.TokenDefinitions.any;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class ArithmeticValueExpressionSemanticsTest extends ParameterizedParse {

    @Parameters(name="{0} ({4})")
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
            { "[signed] 8 / 2 == 4", div, stream(8, 2, 4), signed(), true },
            { "[signed] 1 / 2 == 0", div, stream(1, 2, 0), signed(), true },
            { "[signed] 7 / 8 == 0", div, stream(7, 8, 0), signed(), true },
            { "[signed] 3 / 2 == 1", div, stream(3, 2, 1), signed(), true },
            { "[signed] 1 / 1 == 1", div, stream(1, 1, 1), signed(), true },
            { "[signed] 4 / 2 == 1", div, stream(4, 2, 1), signed(), false },
            { "[signed] 5 / 0 == 0", div, stream(5, 0, 0), signed(), false },
            { "[signed] 2 * 2 == 4", mul, stream(2, 2, 4), signed(), true },
            { "[signed] 0 * 42 == 0", mul, stream(0, 42, 0), signed(), true },
            { "[signed] 42 * 0 == 0", mul, stream(42, 0, 0), signed(), true },
            { "[signed] 1 * 1 == 1", mul, stream(1, 1, 1), signed(), true },
            { "[signed] 0 * 0 == 0", mul, stream(0, 0, 0), signed(), true },
            { "[unsigned] 42 * 42 == 1764", mul2, stream(42,42,6,228), enc(), true },
            { "[little endian] 42 * 42 == 1764", mul2, stream(42,42,228,6), le(), true },
            { "[signed] 2 * 3 == 8", mul, stream(2, 3, 8), signed(), false },
            { "[signed] 8 - 2 == 6", sub, stream(8, 2, 6), signed(), true },
            { "[signed] 3 - 10 == -7", sub, stream(3, 10, -7), signed(), true },
            { "[signed] 0 - 42 == -42", sub, stream(0, 42, -42), signed(), true },
            { "[signed] -42 - 10 == -52", sub, stream(-42, 10, -52), signed(), true },
            { "[signed] -42 - -10 == -32", sub, stream(-42, -10, -32), signed(), true },
            { "[signed] -42 - 42 == 0", sub, stream(-42, 42, 0), signed(), false },
            { "[signed] 10 % 5 == 0", mod, stream(10, 5, 0), signed(), true },
            { "[signed] 10 % 4 == 2", mod, stream(10, 4, 2), signed(), true },
            { "[signed] 10 % 10 == 0", mod, stream(10, 10, 0), signed(), true },
            { "[signed] -10 % 5 == 0", mod, stream(-10, 5, 0), signed(), true },
            { "[signed] -10 % 4 == 2", mod, stream(-10, 4, 2), signed(), true },
            { "[signed] 10 % -5 == 0", mod, stream(10, -5, 0), signed(), false },
            { "[signed] -(1) == -1", neg, stream(1, -1), signed(), true },
            { "[signed] -(2) == -2", neg, stream(2, -2), signed(), true },
            { "[signed] -(3) == -3", neg, stream(3, -3), signed(), true },
            { "[signed] -(0) == 0", neg, stream(0, 0), signed(), true },
            { "[signed] -(4) == 4", neg, stream(4, 4), signed(), false },
            { "[signed] -(-5) == -5", neg, stream(-5, -5), signed(), false }
        });
    }

    public ArithmeticValueExpressionSemanticsTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

    private static Token add = binaryValueExpressionToken(add(ref("a"), ref("b")), 1);
    private static Token div = binaryValueExpressionToken(div(ref("a"), ref("b")), 1);
    private static Token mul = binaryValueExpressionToken(mul(ref("a"), ref("b")), 1);
    private static Token mul2 = binaryValueExpressionToken(mul(ref("a"), ref("b")), 2);
    private static Token sub = binaryValueExpressionToken(sub(ref("a"), ref("b")), 1);
    private static Token mod = binaryValueExpressionToken(mod(ref("a"), ref("b")), 1);
    private static Token neg = unaryValueExpressionToken(neg(ref("a")));

    private static Token singleToken(final String firstName, final String secondName, final int resultSize, final ValueExpression ve) {
        return seq(any(firstName),
                   def(secondName, con(resultSize), eqNum(ve)));
    }

    private static Token binaryValueExpressionToken(final BinaryValueExpression bve, final int resultSize) {
        return seq(any("a"),
                   singleToken("b", "c", resultSize, bve));
    }

    private static Token unaryValueExpressionToken(final UnaryValueExpression uve) {
        return singleToken("a", "b", 1, uve);
    }

}
