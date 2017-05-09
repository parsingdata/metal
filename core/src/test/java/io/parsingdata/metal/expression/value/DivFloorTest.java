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

package io.parsingdata.metal.expression.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.divFloor;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.util.EnvironmentFactory;

public class DivFloorTest {
    private static final Encoding ENC = new Encoding(Sign.SIGNED);

    @Test
    public void testFloorBothPositive() throws IOException {
        final Environment env = EnvironmentFactory.stream(8, 3);
        final ParseResult result = seq(def("left", 1), def("right", 1)).parse(env, ENC);
        assertEquals(Math.floor(8.0 / 3.0), divFloor(ref("left"), ref("right")).eval(result.environment, ENC).head.get().asNumeric().intValue(), 0);
        assertEquals(Math.floor(8.0 / 3.0), div(ref("left"), ref("right")).eval(result.environment, ENC).head.get().asNumeric().intValue(), 0);
    }

    @Test
    public void testFloorBothNegative() throws IOException {
        final Environment env = EnvironmentFactory.stream(-8, -3);
        final ParseResult result = seq(def("left", 1), def("right", 1)).parse(env, ENC);
        assertEquals(Math.floor(-8.0 / -3.0), divFloor(ref("left"), ref("right")).eval(result.environment, ENC).head.get().asNumeric().intValue(), 0);
        assertEquals(Math.floor(-8.0 / -3.0), div(ref("left"), ref("right")).eval(result.environment, ENC).head.get().asNumeric().intValue(), 0);
    }

    @Test
    public void testFloorLeftNegative() throws IOException {
        final Environment env = EnvironmentFactory.stream(-8, 3);
        final ParseResult result = seq(def("left", 1), def("right", 1)).parse(env, ENC);
        assertEquals(Math.floor(-8.0 / 3.0), divFloor(ref("left"), ref("right")).eval(result.environment, ENC).head.get().asNumeric().intValue(), 0);
        // Test to prove that div gives a different result compared to divFloor
        assertNotEquals(Math.floor(-8.0 / 3.0), div(ref("left"), ref("right")).eval(result.environment, ENC).head.get().asNumeric().intValue(), 0);
    }

    @Test
    public void testFloorRightNegative() throws IOException {
        final Environment env = EnvironmentFactory.stream(8, -3);
        final ParseResult result = seq(def("left", 1), def("right", 1)).parse(env, ENC);
        assertEquals(Math.floor(8.0 / -3.0), divFloor(ref("left"), ref("right")).eval(result.environment, ENC).head.get().asNumeric().intValue(), 0);
        // Test to prove that div gives a different result compared to divFloor
        assertNotEquals(Math.floor(8.0 / -3.0), div(ref("left"), ref("right")).eval(result.environment, ENC).head.get().asNumeric().intValue(), 0);
    }

    @Test
    public void testFloorZero() throws IOException {
        final Environment env = EnvironmentFactory.stream(8, 0);
        final ParseResult result = seq(def("left", 1), def("right", 1)).parse(env, ENC);
        assertFalse(divFloor(ref("left"), ref("right")).eval(result.environment, ENC).head.isPresent());
    }
}
