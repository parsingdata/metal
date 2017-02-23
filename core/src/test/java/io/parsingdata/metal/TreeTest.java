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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.transformation.Reversal;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;

@RunWith(JUnit4.class)
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

    private final Optional<Environment> regular;
    private final Optional<Environment> cyclic;

    public TreeTest() throws IOException {
        regular = TREE.parse(stream(HEAD, 0, 6, 10, 8, 8, HEAD, 1, 16, 20, HEAD, 2, 24, 28, 8, 8, HEAD, 3, 0, 0, HEAD, 4, 0, 0, HEAD, 5, 0, 0, HEAD, 6, 0, 0), enc());
                                 /* *--------+---+        *---------+---+  *---------+---+        *--------*--*  *--------*--*  *--------*--*  *--------*--*
                                  *          \---|--------/         \---|--|---------|---|--------/              |              |              |
                                  *              \----------------------|--/         \---|-----------------------|--------------/              |
                                  *                                     \----------------|-----------------------/                             |
                                  *                                                      \-----------------------------------------------------/
                                  */
        cyclic = TREE.parse(stream(HEAD, 0, 4, 8, HEAD, 1, 8, 0, HEAD, 2, 4, 0), enc());
                                /* *--------+--+  *--------+--*  *--------+--*
                                 *          \--|--/        \-----/        |
                                 *             \--|--------------/        |
                                 *                \-----------------------/
                                 */
    }

    @Test
    public void checkRegularTree() {
        assertTrue(regular.isPresent());
        checkStructure(Reversal.reverse(regular.get().order).head.asGraph(), 0);
    }

    @Test
    public void checkCyclicTree() {
        assertTrue(cyclic.isPresent());
        checkStructure(Reversal.reverse(cyclic.get().order).head.asGraph(), 0);
    }

    private void checkStructure(final ParseGraph graph, final long offset) {
        checkStructure(graph, graph, offset);
    }

    private void checkStructure(final ParseGraph root, final ParseGraph graph, final long offset) {
        checkHeader(graph, offset);
        final ParseItem left = graph.tail.tail.head.asGraph().head;
        long leftOffset = 0;
        if (!(left.isValue() && left.asValue().matches("left_terminator"))) {
            assertTrue(left.isGraph());
            leftOffset = left.asGraph().head.asValue().asNumeric().longValue();
            if (leftOffset != 0) {
                final ParseItem leftItem = graph.tail.tail.head.asGraph().head.asGraph().tail.head.asGraph();
                checkBranch(root, leftOffset, leftItem);
            }
        }

        final ParseItem right = leftOffset != 0 ? graph.tail.tail.tail.head.asGraph().head : graph.tail.tail.tail.head.asGraph().head;
        if (!(right.isValue() && right.asValue().matches("right_terminator"))) {
            assertTrue(right.isGraph());
            final long rightOffset = right.asGraph().head.asValue().asNumeric().longValue();
            if (rightOffset != 0) {
                final ParseItem rightItem = (leftOffset != 0 ? graph.tail.tail.tail.head.asGraph().head.asGraph().tail : graph.tail.tail.head.asGraph().head.asGraph().tail);
                final ParseItem rightSeq = rightItem.asGraph().head;
                checkBranch(root, rightOffset, rightSeq);
            }
        }
    }

    private void checkBranch(final ParseGraph root, final long offset, final ParseItem item) {
        assertFalse(item.isValue());
        if (item.asGraph().head.isGraph()) {
            checkStructure(root, item.asGraph().head.asGraph(), offset);
        } else if (item.asGraph().head.isReference()) {
            checkHeader(item.asGraph().head.asReference().resolve(root).asGraph(), offset);
        }
    }

    private void checkHeader(final ParseGraph graph, final long offset) {
        final ParseItem head = graph.head;
        assertTrue(head.isValue());
        assertEquals(HEAD, head.asValue().asNumeric().intValue());
        assertEquals(offset, head.asValue().slice.offset);
        final ParseItem nr = graph.tail.head;
        assertTrue(nr.isValue());
    }

    @Test
    public void checkRegularTreeFlat() {
        assertTrue(regular.isPresent());
        final ImmutableList<Value> nrs = getAllValues(regular.get().order, "nr");
        for (int i = 0; i < 7; i++) {
            assertTrue(contains(nrs, i));
        }
    }

    private boolean contains(final ImmutableList<Value> nrs, final int i) {
        if (nrs.isEmpty()) { return false; }
        if (nrs.head.asNumeric().intValue() == i) { return true; }
        if (nrs.tail != null) { return contains(nrs.tail, i); }
        return false;
    }

}
