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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.transformation.Reversal;
import io.parsingdata.metal.token.Token;

@RunWith(JUnit4.class)
public class TreeTest {

    private static final int HEAD = 9;
    private static final Token TREE =
        seq("tree",
            def("head", con(1), eq(con(HEAD))),
            def("nr", con(1)),
            def("left", con(1)),
            pre(sub(token("tree"), last(ref("left"))), not(eq(last(ref("left")), con(0)))),
            def("right", con(1)),
            pre(sub(token("tree"), last(ref("right"))), not(eq(last(ref("right")), con(0))))
        );

    private final ParseResult regular;
    private final ParseResult cyclic;

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
    public void checkRegularTree() throws IOException {
        assertTrue(regular.succeeded);
        checkStructure(Reversal.reverse(regular.environment.order).head.asGraph(), 0);
    }

    @Test
    public void checkCyclicTree() throws IOException {
        assertTrue(cyclic.succeeded);
        checkStructure(Reversal.reverse(cyclic.environment.order).head.asGraph(), 0);
    }

    private void checkStructure(final ParseGraph graph, final long offset) throws IOException {
        checkStructure(graph, graph, offset);
    }

    private void checkStructure(final ParseGraph root, final ParseGraph graph, final long offset) throws IOException {
        checkHeader(graph, offset);
        final ParseItem left = graph.tail.tail.head;
        assertTrue(left.isValue());
        final long leftOffset = left.asValue().asNumeric().longValue();
        if (leftOffset != 0) {
            final ParseItem leftItem = graph.tail.tail.tail.head.asGraph().head;
            checkBranch(root, leftOffset, leftItem);
        }

        final ParseItem right = leftOffset != 0 ? graph.tail.tail.tail.tail.head : graph.tail.tail.tail.head;
        assertTrue(right.isValue());
        final long rightOffset = right.asValue().asNumeric().longValue();
        if (rightOffset != 0) {
            final ParseItem rightItem = (leftOffset != 0 ? graph.tail.tail.tail.tail.tail.head : graph.tail.tail.tail.tail.head);
            final ParseItem rightSeq = rightItem.asGraph().head;
            checkBranch(root, rightOffset, rightSeq);
        }
    }

    private void checkBranch(final ParseGraph root, final long offset, final ParseItem item) throws IOException {
        assertFalse(item.isValue());
        if (item.asGraph().head.isGraph()) {
            checkStructure(root, item.asGraph().head.asGraph(), offset);
        } else if (item.asGraph().head.isReference()) {
            checkHeader(item.asGraph().head.asReference().resolve(root).asGraph(), offset);
        }
    }

    private void checkHeader(final ParseGraph graph, final long offset) throws IOException {
        final ParseItem head = graph.head;
        assertTrue(head.isValue());
        assertEquals(HEAD, head.asValue().asNumeric().intValue());
        assertEquals(offset, head.asValue().source.offset);
        final ParseItem nr = graph.tail.head;
        assertTrue(nr.isValue());
    }

    @Test
    public void checkRegularTreeFlat() throws IOException {
        assertTrue(regular.succeeded);
        final ImmutableList<ParseValue> nrs = getAllValues(regular.environment.order, "nr");
        for (int i = 0; i < 7; i++) {
            assertTrue(contains(nrs, i));
        }
    }

    private boolean contains(final ImmutableList<ParseValue> nrs, final int i) throws IOException {
        if (nrs.isEmpty()) { return false; }
        if (nrs.head.asNumeric().intValue() == i) { return true; }
        if (nrs.tail != null) { return contains(nrs.tail, i); }
        return false;
    }

}
