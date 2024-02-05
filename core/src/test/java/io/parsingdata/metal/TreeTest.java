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

package io.parsingdata.metal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Token;

public class TreeTest {

    private static final int HEAD = 9;
    private static final Token TREE =
        seq("tree",
            def("head", con(1), eq(con(HEAD))),
            def("nr", con(1)),
            cho(def("left_terminator", con(1), eq(con(0))),
                seq(def("left", con(1)),
                    sub(token("tree"), last(ref("left")))
                )
            ),
            cho(def("right_terminator", con(1), eq(con(0))),
                seq(def("right", con(1)),
                    sub(token("tree"), last(ref("right")))
                )
            )
        );

    private final ParseState regular;
    private final ParseState cyclic;

    public TreeTest() {
        regular = TREE.parse(env(stream(HEAD, 0, 6, 10, 8, 8, HEAD, 1, 16, 20, HEAD, 2, 24, 28, 8, 8, HEAD, 3, 0, 0, HEAD, 4, 0, 0, HEAD, 5, 0, 0, HEAD, 6, 0, 0))).get();
                                     /* *--------+---+        *---------+---+  *---------+---+        *--------*--*  *--------*--*  *--------*--*  *--------*--*
                                      *          \---|--------/         \---|--|---------|---|--------/              |              |              |
                                      *              \----------------------|--/         \---|-----------------------|--------------/              |
                                      *                                     \----------------|-----------------------/                             |
                                      *                                                      \-----------------------------------------------------/
                                      */
        cyclic = TREE.parse(env(stream(HEAD, 0, 4, 8, HEAD, 1, 8, 0, HEAD, 2, 4, 0))).get();
                                    /* *--------+--+  *--------+--*  *--------+--*
                                     *          \--|--/        \-----/        |
                                     *             \--|--------------/        |
                                     *                \-----------------------/
                                     */
    }

    @Test
    public void checkRegularTree() {
        checkStructure(regular);
    }

    @Test
    public void checkCyclicTree() {
        checkStructure(cyclic);
    }

    private void checkStructure(final ParseState parseState) {
        final ParseGraph input = parseState.order.head.asGraph(); // order = top-level ParseGraph, head = top-level Seq
        checkStructure(input, input, 0);
    }

    private void checkStructure(final ParseGraph graph, final ParseGraph root, final int offset) {
        checkHeader(graph.tail.tail, offset); // tail = Seq, tail = Seq
        checkBranch(graph.tail, root, "left"); // left
        checkBranch(graph, root, "right"); // right
    }

    private void checkHeader(final ParseGraph header, final int offset) {
        final ParseItem head = header.tail.head.asGraph().head; // tail = Seq, head = Post, head = Def("head")
        assertTrue(head.isValue());
        assertTrue(head.asValue().matches("head"));
        assertEquals(HEAD, head.asValue().asNumeric().intValueExact());
        assertEquals(offset, head.asValue().slice().offset.intValueExact());
        final ParseItem nr = header.head; // head = Def("nr")
        assertTrue(nr.isValue());
        assertTrue(nr.asValue().matches("nr"));
    }

    private void checkBranch(final ParseGraph branch, final ParseGraph root, final String name) {
        assertTrue(branch.isGraph()); // Seq
        assertTrue(branch.asGraph().definition.name.endsWith("tree"));
        assertTrue(branch.head.isGraph()); // Cho
        assertTrue(branch.head.asGraph().head.isGraph()); // Seq for pointer, Post for terminator
        final ParseGraph subStruct = branch.head.asGraph().head.asGraph();
        if (subStruct.tail.isEmpty()) { // If the graph has only one entry, it's a single Post for the terminator
            assertTrue(subStruct.head.isValue()); // Def("[left|right]_terminator")
            assertTrue(subStruct.head.asValue().matches(name + "_terminator"));
        } else { // Otherwise, it's a Seq with a pointer and a Sub
            assertTrue(subStruct.tail.head.isValue()); // Def("[left|right]")
            assertTrue(subStruct.tail.head.asValue().matches(name));
            final int pointer = subStruct.tail.head.asValue().asNumeric().intValueExact();
            assertTrue(subStruct.head.isGraph()); // Sub
            if (subStruct.head.asGraph().head.isReference()) { // If the Sub contains a Reference, it's a cycle
                checkResolve(subStruct.head.asGraph().head.asReference(), root, pointer);
            } else {
                checkStructure(subStruct.head.asGraph().head.asGraph(), root, pointer); // Recurse
            }
        }
    }

    private void checkResolve(final ParseReference reference, final ParseGraph root, final int offset) {
        checkHeader(reference.resolve(root).get().asGraph().tail.tail, offset); // Only check header on cycle to prevent loop
    }

    @Test
    public void checkRegularTreeFlat() {
        final ImmutableList<ParseValue> nrs = getAllValues(regular.order, "nr");
        for (int i = 0; i < 7; i++) {
            assertTrue(contains(nrs, i));
        }
    }

    private boolean contains(final ImmutableList<ParseValue> nrs, final int i) {
        if (nrs.isEmpty()) {
            return false;
        }
        if (nrs.head().asNumeric().intValueExact() == i) {
            return true;
        }
        if (nrs.tail() != null) {
            return contains(nrs.tail(), i);
        }
        return false;
    }

}
