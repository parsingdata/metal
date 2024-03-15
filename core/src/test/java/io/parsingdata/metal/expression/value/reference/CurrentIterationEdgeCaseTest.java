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
package io.parsingdata.metal.expression.value.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.iteration;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.expression.value.Value;

public class CurrentIterationEdgeCaseTest {

    private ParseState parseState;

    @BeforeEach
    public void before() {
        parseState = rep(any("a")).parse(env(stream(1, 2, 3))).get();
    }

    @Test
    public void emptyLevel() {
        Optional<Value> result = iteration(last(ref("b"))).evalSingle(parseState, enc());
        assertTrue(result.isPresent());
        assertEquals(NOT_A_VALUE, result.get());
    }

    @Test
    public void notAValueLevel() {
        Optional<Value> result = iteration(last(div(con(1), con(0)))).evalSingle(parseState, enc());
        assertTrue(result.isPresent());
        assertEquals(NOT_A_VALUE, result.get());
    }

    @Test
    public void negativeLevel() {
        Optional<Value> result = iteration(con(-1, signed())).evalSingle(parseState, enc());
        assertTrue(result.isPresent());
        assertEquals(NOT_A_VALUE, result.get());
    }
}
