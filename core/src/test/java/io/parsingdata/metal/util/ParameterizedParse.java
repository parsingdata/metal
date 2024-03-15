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

package io.parsingdata.metal.util;

import static java.math.BigInteger.ONE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static io.parsingdata.metal.util.EnvironmentFactory.env;

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ParameterizedParse {

    /**
     * Each item in the collection should contain exactly 5 values of the following type:
     * <ul>
     *     <li>String: description of the test</li>
     *     <li>Token: token under test</li>
     *     <li>ParseState: the parse state to test the token against</li>
     *     <li>Encoding: the encoding of the token</li>
     *     <li>boolean: true if token should parse, false otherwise</li>
     * </ul>
     *
     * @return a collection of test arguments.
     */
    public abstract Collection<Object[]> data();

    @ParameterizedTest(name="{0} ({4})")
    @MethodSource("data")
    public void test(final String description, final Token token, final ParseState parseState, final Encoding encoding, final boolean result) {
        Optional<ParseState> endState = token.parse(env(parseState, encoding));
        assertEquals(result, endState.isPresent());

        endState.ifPresent(state -> {
            // In case parsing succeeded we expect to have parsed the whole stream and we shouldn't be able to slice 1 byte.
            assertFalse(state.slice(ONE).isPresent(), "The test has not parsed the whole stream. It ended at offset " + state.offset + ".");
        });
    }

}
