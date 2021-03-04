/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

import static java.math.BigInteger.ZERO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.EMPTY;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.first;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.scope;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
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
        thrown.expectMessage("Argument scopeSize must evaluate to a positive, countable value.");
        new Scope(con(0), EMPTY_SVE).eval(EMPTY_PARSE_STATE, enc());
    }

    @Test
    public void negativeScopeSize() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument scopeSize must evaluate to a positive, countable value.");
        new Scope(con(0), con(-1, signed())).eval(EMPTY_PARSE_STATE, enc());
    }

    @Test
    public void scopeSizes() {
        final Token scopesToken =
            seq(any("value"), // 0
                repn(seq(any("value"),  // 1
                        seq(any("value"), // 2
                            // deepestValue=2, scope=0, so refers to the deepest scope, which includes only the "value" in this seq
                            def("deepestValue", con(1), eq(first(scope(ref("value"), con(0))))),
                            // middleValue=1, scope=1, so includes the current seq and the one above it.
                            def("middleValue", con(1), eq(first(scope(ref("value"), con(1))))),
                            // stillMiddleValue=1, scope=2, but since repn is also a scope delimiter, it does not include the top-level seq
                            def("stillMiddleValue", con(1), eq(first(scope(ref("value"), con(2))))),
                            // topValue=0, scope=3, includes all scope delimiting tokens, to effectively global
                            def("topValue", con(1), eq(first(scope(ref("value"), con(3))))),
                            // hugeScope=0, scope=100, everything from 3 up is global scope since there are 4 scope delimiting tokens (0-3)
                            def("hugeScope", con(1), eq(first(scope(ref("value"), con(100)))))
                        )
                    ), con(1)
                )
            );
        final Optional<ParseState> result = scopesToken.parse(env(stream(0, 1, 2, 2, 1, 1, 0, 0), enc()));
        assertTrue(result.isPresent());
    }

    @Test
    public void parseGraphWithEmptyBranch() {
        final Token A =
            seq("A",
                repn("data", def("data", con(1)), first(con(5))),
                EMPTY);
        final Token S2 =
            seq("S",
                seq("a",
                    A,
                    EMPTY),
                tie("dx", def("dx_", con(1)), scope(ref("S.a.A.data.data"), con(0))));
        final Optional<ParseState> result = S2.parse(env(stream(0, 0, 0, 0, 0), enc()));
        assertEquals(5L, ref("dx_").eval(result.get(), enc()).size);
    }

    @Test
    public void parseGraphWithEmptyBranchSimplified() {
        final Optional<ParseState> result = def("a", first(scope(con(1), con(0)))).parse(env(stream(0)));
        assertEquals(ZERO, ref("a").eval(result.get(), enc()).head.asNumeric());
    }

}
