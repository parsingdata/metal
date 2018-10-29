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

package io.parsingdata.metal;

import static java.math.BigInteger.ONE;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class ScopeTest {

    final Token nested =
        seq("nested",
            def("left", 1),
            token("nestedOrTerminator"),
            def("right", 1, eq(nth(ref("left"), sub(count(ref("left")), count(ref("right"))))))
        );

    final Token terminator =
        def("terminator", 1, eq(con(42)));

    final Token nestedOrTerminator =
        cho("nestedOrTerminator",
            nested,
            terminator);

    final Token format =
        seq(
            // We need to parse "nestedOrTerminator" with a "terminator" match before we attempt to parse "nested"
            // because of how TokenRef works, but this is just a workaround to simplify the test code.
            nestedOrTerminator,
            nested
        );

    @Test
    public void nestedScopes() {
        Optional<ParseState> parseState = format.parse(env(stream(42, 1, 2, 3, 42, 3, 2, 1), enc()));
        assertTrue(parseState.isPresent());
        assertFalse("The test has not parsed the whole stream. It ended at offset " + parseState.get().offset + ".", parseState.get().slice(ONE).isPresent());
    }

}
