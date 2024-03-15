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
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.scope;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.Shorthand;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;

public class LocalReferenceFollowingScopeTest {

    private static Token followingScope(final Expression tenCountExpression) {
        return
            seq("checkedRep",
                def("magic", con(1), eq(con(42))),
                rep("items",
                    def("ten", con(1), eq(con(10)))
                ),
                def("tenCount", con(1), tenCountExpression)
            );
    }

    private static Token topLevelFollowingScopes(final Expression tenCountExpression) {
        return
            seq("container",
                followingScope(tenCountExpression),
                followingScope(tenCountExpression)
            );
    }

    private void followingScopes(final Expression tenCountExpression) {
        Optional<ParseState> result = topLevelFollowingScopes(tenCountExpression).parse(env(stream(42, 10, 10, 2, 42, 10, 10, 10, 3)));
        assertTrue(result.isPresent());
        assertEquals(9, result.get().offset.intValueExact());
    }

    @Test
    public void testFollowingScopesCalculated() {
        followingScopes(eqNum(sub(count(ref("ten")), sub(fold(ref("tenCount"), Shorthand::add), last(ref("tenCount"))))));
    }

    @Test
    public void testFollowingScopesLocalReferenced() {
        followingScopes(eqNum(count(scope(ref("ten"), con(0)))));
    }

}
