/*
 * Copyright 2013-2019 Netherlands Forensic Institute
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

import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.first;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.scope;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.EMPTY_SVE;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class ScopeTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void notAValueScopeSize() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument scopeSize must evaluate to a positive, non-empty countable value.");
        new Scope(con(0), EMPTY_SVE).eval(EMPTY_PARSE_STATE, enc());
    }

    @Test
    public void negativeScopeSize() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument scopeSize must evaluate to a positive, non-empty countable value.");
        new Scope(con(0), con(-1, signed())).eval(EMPTY_PARSE_STATE, enc());
    }

    @Test
    public void scopeSizes() {
        final Token scopesToken =
            seq(any("value"),
                rep(seq(any("value"),
                        seq(any("value"),
                            def("deepestValue", con(1), eq(first(scope(ref("value"), con(0))))),
                            def("middleValue", con(1), eq(first(scope(ref("value"), con(1))))),
                            def("stillMiddleValue", con(1), eq(first(scope(ref("value"), con(2))))),
                            def("topValue", con(1), eq(first(scope(ref("value"), con(3))))),
                            def("hugeScope", con(1), eq(first(scope(ref("value"), con(100)))))
                        )
                    )
                )
            );
        final Optional<ParseState> result = scopesToken.parse(env(stream(0, 1, 2, 2, 1, 1, 0, 0), enc()));
        assertTrue(result.isPresent());
    }

}
