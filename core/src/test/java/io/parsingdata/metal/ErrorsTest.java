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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static io.parsingdata.metal.AutoEqualityTest.DUMMY_STREAM;
import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.mul;
import static io.parsingdata.metal.Shorthand.neg;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.token.Token;

public class ErrorsTest {

    @Test
    public void noValueForSize() {
        // Basic division by zero.
        final Token nanSize = def("a", div(con(1), con(0)));
        assertFalse(nanSize.parse(env(stream(1))).isPresent());
        // Try to negate division by zero.
        final Token negNanSize = def("a", neg(div(con(1), con(0))));
        assertFalse(negNanSize.parse(env(stream(1))).isPresent());
        // Add one to division by zero.
        final Token addNanSize = def("a", add(div(con(1), con(0)), con(1)));
        assertFalse(addNanSize.parse(env(stream(1))).isPresent());
        // Add division by zero to one.
        final Token addNanSize2 = def("a", add(con(1), div(con(1), con(0))));
        assertFalse(addNanSize2.parse(env(stream(1))).isPresent());
        // Subtract one from division by zero.
        final Token subNanSize = def("a", sub(div(con(1), con(0)), con(1)));
        assertFalse(subNanSize.parse(env(stream(1))).isPresent());
        // Multiply division by zero with one.
        final Token mulNanSize = def("a", mul(div(con(1), con(0)), con(1)));
        assertFalse(mulNanSize.parse(env(stream(1))).isPresent());
    }

    @Test
    public void parseStateWithNegativeOffset() {
        final Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> createFromByteStream(DUMMY_STREAM, BigInteger.valueOf(-1)));
        assertEquals("Argument offset may not be negative.", e.getMessage());
    }

}
