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

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.elvis;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.len;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.mul;
import static io.parsingdata.metal.Shorthand.neg;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.shl;
import static io.parsingdata.metal.Shorthand.shr;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.expression.value.SingleValueExpression;

public class ShorthandOverloadsTest {

    public static final ParseState PARSE_STATE = ParseState.createFromByteStream(new ByteStream() {
        @Override public byte[] read(BigInteger offset, int length) { return new byte[0]; }
        @Override public boolean isAvailable(BigInteger offset, BigInteger length) { return false; }
    });

    public static Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "add", add(con(1), con(1)), con(2) },
            { "div", div(con(12), con(4)), con(3) },
            { "mul", mul(con(3), con(4)), con(12) },
            { "sub", sub(con(4), con(3)), con(1) },
            { "mod", mod(con(6), con(3)), con(0) },
            { "neg", neg(con(0)), con(0) },
            { "and", and(con(1), con(1)), con(1) },
            { "or", or(con(1), con(0)), con(1) },
            { "not", not(con(0)), con(255) },
            { "shl", shl(con(1), con(1)), con(2) },
            { "shr", shr(con(2), con(1)), con(1) },
            { "len", len(con(0)), con(1) },
            { "nth", nth(con(1), con(0)), con(1) },
            { "offset", offset(con(0)), con(0) },
            { "cat", cat(con(0), con(1)), con(0, 1) },
            { "elvis", elvis(con(1), con(2)), con(1) },
        });
    }

    @ParameterizedTest(name="SingleValueExpression {0}")
    @MethodSource("data")
    public void test(final String description, final SingleValueExpression toExecute, final SingleValueExpression expectedResult) {
        assertTrue(eq(toExecute, expectedResult).eval(PARSE_STATE, DEFAULT_ENCODING));
    }

}
