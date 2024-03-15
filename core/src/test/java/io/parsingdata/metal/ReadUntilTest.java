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


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.eq;
import static io.parsingdata.metal.util.TokenDefinitions.notEq;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.token.Token;

public class ReadUntilTest {

    private final Token _readUntil = seq(rep(notEq("other", 42)),
                                         eq("terminator", 42));

    @Test
    public void readUntilConstant() {
        assertTrue(_readUntil.parse(env(stream(1, 2, 3, 4, 42))).isPresent());
    }

    @Test
    public void readUntilNoSkipping() {
        assertTrue(_readUntil.parse(env(stream(42))).isPresent());
    }

    @Test
    public void readUntilErrorNoTerminator() {
        assertFalse(_readUntil.parse(env(stream(1, 2, 3, 4))).isPresent());
    }

}
