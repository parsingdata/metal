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

import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.mul;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.shl;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

public class PITestCasesTest {

    public static final Token MUL = seq(any("a"), any("b"), def("c", 1, eqNum(mul(ref("a"), ref("b")))));
    public static final Token MOD = seq(any("a"), any("b"), def("c", 1, eqNum(mod(ref("a"), ref("b")))));
    public static final Token DIV = seq(any("a"), any("b"), def("c", 1, eqNum(div(ref("a"), ref("b")))));
    public static final Token NOT = seq(any("a"), def("not(a)", con(1), eq(not(ref("a")))));
    public static final Token OR = seq(any("a"), any("b"), def("or(a,b)", con(1), eq(or(ref("a"), ref("b")))));
    public static final Token SHL = seq(any("a"), def("shl(a,1)", con(1), eq(shl(ref("a"), con(1)))));

    @Test
    public void handlePITestIssue() {
        // Taken from ArithmeticValueExpressionSemanticsTest:
        parse(MUL, stream(2, 2, 4), signed());
        parse(MOD, stream(10, 4, 2), signed());
        parse(DIV, stream(6, 3, 2), signed());
        // Taken from BitwiseValueExpressionSemanticsTest:
        parse(NOT, stream(0, 255), enc());
        parse(OR, stream(0, 255, 255), enc());
        parse(SHL, stream(85, 170), enc());
    }

    private void parse(final Token token, final ParseState parseState, final Encoding encoding) {
        assertTrue(token.parse(env(parseState, encoding)).isPresent());
    }

}
