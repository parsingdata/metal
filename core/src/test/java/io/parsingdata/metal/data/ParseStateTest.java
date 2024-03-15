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

package io.parsingdata.metal.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.token.Token;

public class ParseStateTest {

    @Test
    public void closeParseStateWithWrongToken() {
        final Token open = rep("openName", any("a"));
        final Token close = rep("closeName", any("a"));
        final Exception e = Assertions.assertThrows(IllegalStateException.class, () -> stream().addBranch(open).closeBranch(close));
        assertEquals("Cannot close branch for iterable token closeName. Current iteration state is for token openName.", e.getMessage());
    }

}