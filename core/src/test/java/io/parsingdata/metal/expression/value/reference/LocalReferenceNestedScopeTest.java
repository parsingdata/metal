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

import static java.math.BigInteger.ONE;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.first;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.scope;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;

public class LocalReferenceNestedScopeTest {

    private Token nestedScope(final Expression rightExpression) {
        return
            seq("nestedScope",
                def("left", 1),
                token("nestedOrTerminator"),
                def("right", 1, rightExpression)
        );
    }

    private Token topLevelNestedScopes(final Expression rightExpression) {
        return
            seq(
                // We need to parse this cho with a "terminator" match before we attempt to parse "nestedScope"
                // because of how TokenRef works, but this is just a workaround to simplify the test code.
                cho("nestedOrTerminator",
                    nestedScope(rightExpression),
                    def("terminator", 1, eq(con(42)))),
                nestedScope(rightExpression)
        );
    }

    private void nestedScopes(final Expression rightExpression) {
        Optional<ParseState> parseState = topLevelNestedScopes(rightExpression).parse(env(stream(42, 1, 2, 3, 42, 3, 2, 1), enc()));
        assertTrue(parseState.isPresent());
        assertFalse(parseState.get().slice(ONE).isPresent(), "The test has not parsed the whole stream. It ended at offset " + parseState.get().offset + ".");
    }

    @Test
    public void testNestedScopesCalculated() {
        nestedScopes(eq(nth(ref("left"), sub(count(ref("left")), count(ref("right"))))));
    }

    @Test
    public void testNestedScopesLocalReferenced() {
        nestedScopes(eq(first(scope(ref("left"), con(0)))));
    }

}
