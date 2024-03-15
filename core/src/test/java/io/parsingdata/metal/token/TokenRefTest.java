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

package io.parsingdata.metal.token;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import org.junit.jupiter.api.Test;

public class TokenRefTest {

    @Test
    public void nonExistingRefToken() {
        assertFalse(token("a").parse(env(stream(0))).isPresent());
        assertFalse(seq("a", any("b"), token("c")).parse(env(stream(0))).isPresent());
        assertFalse(sub(token("a"), con(0)).parse(env(stream(0))).isPresent());
    }

    @Test
    public void findRightDefinition() {
        // Clearly reference the second:
        assertTrue(createNamedTokens("out", "in", "in").parse(env(stream(21, 42))).isPresent());
        assertTrue(createNamedTokens("out", "in", "in").parse(env(stream(21, 21, 42))).isPresent());
        assertTrue(createNamedTokens("out", "in", "in").parse(env(stream(21, 21, 21, 42))).isPresent());
        // Clearly reference the first:
        assertTrue(createNamedTokens("in", "out", "in").parse(env(stream(21, 42))).isPresent());
        assertTrue(createNamedTokens("in", "out", "in").parse(env(stream(21, 42, 21, 42))).isPresent());
        // Reference the first:
        assertTrue(createNamedTokens("in", "in", "in").parse(env(stream(21, 42))).isPresent());
        assertTrue(createNamedTokens("in", "in", "in").parse(env(stream(21, 42, 21, 42))).isPresent());
        // So that this will fail:
        assertFalse(createNamedTokens("in", "in", "in").parse(env(stream(21, 21, 42))).isPresent());
    }

    private Token createNamedTokens(String firstSeq, String secondSeq, String refName) {
        return
            seq(firstSeq,
                seq(secondSeq,
                    def("value", 1, eq(con(21))),
                    opt(token(refName))
                ),
                def("footer", 1, eq(con(42)))
            );
    }

}
