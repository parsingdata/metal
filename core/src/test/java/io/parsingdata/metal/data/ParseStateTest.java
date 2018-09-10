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

package io.parsingdata.metal.data;

import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.token.Token;

public class ParseStateTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void closeParseStateWithWrongToken() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Cannot close branch for iterable token closeName. Current iteration state is for token openName.");
        final Token open = rep("openName", any("a"));
        final Token close = rep("closeName", any("a"));
        stream().addBranch(open).closeBranch(close);
    }

}