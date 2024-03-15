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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.CURRENT_OFFSET;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.whl;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class DefinitionTest {

    public static final Token DEF_ONE = def("one", 1, eq(con(1)));
    public static final Token DEF_TWO = def("two", 1, eq(con(2)));
    public static final Token CHO_12 = cho(DEF_ONE, DEF_TWO);
    public static final Token REP_1 = rep(DEF_ONE);
    public static final Token OPT_2 = opt(DEF_TWO);
    public static final Token PRE_1 = pre(DEF_ONE, eq(con(0), con(0)));
    public static final Token REPN_1 = repn(DEF_ONE, con(1));
    public static final Token SUB_2 = sub(DEF_TWO, CURRENT_OFFSET);
    public static final Token WHL_1 = whl(DEF_ONE, eqNum(con(0), count(ref("one"))));
    public static final Token COMPOSED = seq(WHL_1, REPN_1, REP_1, CHO_12, OPT_2, PRE_1, SUB_2);

    @Test
    public void composed() {
        final Optional<ParseState> result = COMPOSED.parse(env(stream(1, 1, 1, 2, 2, 1, 2)));
        assertTrue(result.isPresent());
        final ParseGraph graph = result.get().order;
        assertEquals(NONE, graph.getDefinition());
        assertEquals(COMPOSED, graph.head.getDefinition());
        assertEquals(SUB_2, graph.head.asGraph().head.getDefinition());
        assertEquals(DEF_TWO, graph.head.asGraph().head.asGraph().head.getDefinition());
        assertEquals(COMPOSED, graph.head.asGraph().tail.getDefinition());
        assertEquals(PRE_1, graph.head.asGraph().tail.head.getDefinition());
        assertEquals(DEF_ONE, graph.head.asGraph().tail.head.asGraph().head.getDefinition());
        assertEquals(COMPOSED, graph.head.asGraph().tail.tail.getDefinition());
        assertEquals(OPT_2, graph.head.asGraph().tail.tail.head.getDefinition());
        assertEquals(DEF_TWO, graph.head.asGraph().tail.tail.head.asGraph().head.getDefinition());
        assertEquals(COMPOSED, graph.head.asGraph().tail.tail.tail.getDefinition());
        assertEquals(CHO_12, graph.head.asGraph().tail.tail.tail.head.getDefinition());
        assertEquals(DEF_TWO, graph.head.asGraph().tail.tail.tail.head.asGraph().head.getDefinition());
        assertEquals(COMPOSED, graph.head.asGraph().tail.tail.tail.tail.getDefinition());
        assertEquals(REP_1, graph.head.asGraph().tail.tail.tail.tail.head.getDefinition());
        assertEquals(DEF_ONE, graph.head.asGraph().tail.tail.tail.tail.head.asGraph().head.getDefinition());
        assertEquals(COMPOSED, graph.head.asGraph().tail.tail.tail.tail.tail.getDefinition());
        assertEquals(REPN_1, graph.head.asGraph().tail.tail.tail.tail.tail.head.getDefinition());
        assertEquals(DEF_ONE, graph.head.asGraph().tail.tail.tail.tail.tail.head.asGraph().head.getDefinition());
        assertEquals(COMPOSED, graph.head.asGraph().tail.tail.tail.tail.tail.tail.getDefinition());
        assertEquals(WHL_1, graph.head.asGraph().tail.tail.tail.tail.tail.tail.head.getDefinition());
        assertEquals(DEF_ONE, graph.head.asGraph().tail.tail.tail.tail.tail.tail.head.asGraph().head.getDefinition());
    }

}
