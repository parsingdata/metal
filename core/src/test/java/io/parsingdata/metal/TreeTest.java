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

import io.parsingdata.metal.data.*;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

@RunWith(JUnit4.class)
public class TreeTest {

    private static final int HEAD = 9;
    private static final Token TREE =
        new Token(null) {
            @Override
            protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
            return seq(def("head", con(1), eq(con(HEAD))),
                       def("nr", con(1)),
                       def("left", con(1)),
                       pre(sub(this, last(ref("left"))), not(eq(last(ref("left")), con(0)))),
                       def("right", con(1)),
                       pre(sub(this, last(ref("right"))), not(eq(last(ref("right")), con(0))))).parse(scope, env, enc);
            }
        };

    private final ParseResult _regular;
    private final ParseResult _cyclic;

    public TreeTest() throws IOException {
        _regular = TREE.parse(stream(HEAD, 0, 6, 10, 8, 8, HEAD, 1, 16, 20, HEAD, 2, 24, 28, 8, 8, HEAD, 3, 0, 0, HEAD, 4, 0, 0, HEAD, 5, 0, 0, HEAD, 6, 0, 0), enc());
                                  /* *--------+---+        *---------+---+  *---------+---+        *--------*--*  *--------*--*  *--------*--*  *--------*--*
                                   *          \---|--------/         \---|--|---------|---|--------/              |              |              |
                                   *              \----------------------|--/         \---|-----------------------|--------------/              |
                                   *                                     \----------------|-----------------------/                             |
                                   *                                                      \-----------------------------------------------------/
                                   */
        _cyclic = TREE.parse(stream(HEAD, 0, 4, 8, HEAD, 1, 8, 0, HEAD, 2, 4, 0), enc());
                                 /* *--------+--+  *--------+--*  *--------+--*
                                  *          \--|--/        \-----/        |
                                  *             \--|--------------/        |
                                  *                \-----------------------/
                                  */
    }

    @Test
    public void checkRegularTree() {
        Assert.assertTrue(_regular.succeeded());
        checkStruct(_regular.getEnvironment().order.reverse(), 0);
    }

    @Test
    public void checkCyclicTree() {
        Assert.assertTrue(_cyclic.succeeded());
        checkStruct(_cyclic.getEnvironment().order.reverse(), 0);
    }

    private void checkStruct(final ParseGraph graph, final long offset) {
        checkStruct(graph, graph.head.asGraph(), offset);
    }

    private void checkStruct(final ParseGraph root, final ParseGraph graph, final long offset) {
        checkHeader(graph, offset);
        final ParseItem left = graph.tail.tail.head;
        Assert.assertTrue(left.isValue());
        final long leftOffset = left.asValue().asNumeric().longValue();
        if (leftOffset != 0) {
            final ParseItem leftItem = graph.tail.tail.tail.head.asGraph().head;
            checkBranch(root, leftOffset, leftItem);
        }

        final ParseItem right = leftOffset != 0 ? graph.tail.tail.tail.tail.head : graph.tail.tail.tail.head;
        Assert.assertTrue(right.isValue());
        final long rightOffset = right.asValue().asNumeric().longValue();
        if (rightOffset != 0) {
            final ParseItem rightItem = (leftOffset != 0 ? graph.tail.tail.tail.tail.tail.head : graph.tail.tail.tail.tail.head);
            final ParseItem rightSeq = rightItem.asGraph().head;
            checkBranch(root, rightOffset, rightSeq);
        }
    }

    private void checkBranch(final ParseGraph root, final long offset, final ParseItem item) {
        Assert.assertFalse(item.isValue());
        if (item.asGraph().head.isGraph()) {
            checkStruct(root, item.asGraph().head.asGraph(), offset);
        } else if (item.asGraph().head.isRef()) {
            checkHeader(item.asGraph().head.asRef().resolve(root), offset);
        }
    }

    private void checkHeader(final ParseGraph graph, final long offset) {
        final ParseItem head = graph.head;
        Assert.assertTrue(head.isValue());
        Assert.assertEquals(HEAD, head.asValue().asNumeric().intValue());
        Assert.assertEquals(offset, head.asValue().getOffset());
        final ParseItem nr = graph.tail.head;
        Assert.assertTrue(nr.isValue());
    }

    @Test
    public void checkRegularTreeFlat() {
        Assert.assertTrue(_regular.succeeded());
        final ParseValueList nrs = ByName.getAllValues(_regular.getEnvironment().order, "nr");
        for (int i = 0; i < 7; i++) {
            Assert.assertTrue(contains(nrs, i));
        }
    }

    private boolean contains(final ParseValueList nrs, final int i) {
        if (nrs.isEmpty()) { return false; }
        if (nrs.head.asNumeric().intValue() == i) { return true; }
        if (nrs.tail != null) { return contains(nrs.tail, i); }
        return false;
    }

}
