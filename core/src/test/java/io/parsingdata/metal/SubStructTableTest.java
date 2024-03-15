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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class SubStructTableTest {

    private final Token struct =
        seq(def("header", con(1), eq(con(42))),
            def("footer", con(1), eq(con(84))));

    private final Token table =
        seq(def("tableSize", con(1)),
            repn(def("pointer", con(1)), last(ref("tableSize"))),
            sub(struct, ref("pointer")));

    @Test
    public void table() {
        final ParseState parseState = stream(3, 6, 4, 9, 42, 84, 42, 84, 0, 42, 84);
                                  /* offset: 0, 1, 2, 3,  4,  5,  6,  7, 8,  9, 10
                                   * count:  ^
                                   * pointers:  ^, ^, ^
                                   * ref1:      +----------------^^--^^
                                   * ref2:         +-----^^--^^
                                   * ref3:            +---------------------^^--^^
                                   */
        final Optional<ParseState> result = table.parse(env(parseState, enc()));
        assertTrue(result.isPresent());
        assertEquals(4, result.get().offset.intValueExact());
        final ParseGraph graph = result.get().order;
        checkStruct(graph.head.asGraph().head.asGraph().head.asGraph(), 6);
        checkStruct(graph.head.asGraph().head.asGraph().tail.head.asGraph(), 4);
        checkStruct(graph.head.asGraph().head.asGraph().tail.tail.head.asGraph(), 9);
    }

    @Test
    public void tableWithDuplicate() {
        final ParseState parseState = stream(4, 7, 5, 5, 10, 42, 84, 42, 84, 0, 42, 84);
                                  /* offset: 0, 1, 2, 3,  4,  5,  6,  7, 8,  9, 10, 11
                                   * count:  ^
                                   * pointers:  ^, ^, ^, ^^
                                   * ref1:      +--------------------^^--^^
                                   * ref2:         +---------^^--^^
                                   * ref3:         +---------^^--^^ duplicate!
                                   * ref4:               ++---------------------^^--^^
                                   */
        final Optional<ParseState> result = table.parse(env(parseState, enc()));
        assertTrue(result.isPresent());
        assertEquals(5, result.get().offset.intValueExact());
        final ParseGraph graph = result.get().order;
        checkStruct(graph.head.asGraph().head.asGraph().head.asGraph(), 7);
        assertTrue(graph.head.asGraph().head.asGraph().tail.head.isReference());
        checkStruct(graph.head.asGraph().head.asGraph().tail.head.asReference().resolve(graph).get().asGraph(), 5);
        checkStruct(graph.head.asGraph().head.asGraph().tail.tail.head.asGraph(), 5);
        checkStruct(graph.head.asGraph().head.asGraph().tail.tail.tail.head.asGraph(), 10);
    }

    private void checkStruct(final ParseGraph graph, final int offsetHeader) {
        assertTrue(graph.head.isGraph());
        assertEquals(84, graph.head.asGraph().head.asValue().asNumeric().intValueExact());
        assertEquals(42, graph.tail.head.asGraph().head.asValue().asNumeric().intValueExact());
        assertEquals(offsetHeader, graph.tail.head.asGraph().head.asValue().slice().offset.intValueExact());
    }

}
