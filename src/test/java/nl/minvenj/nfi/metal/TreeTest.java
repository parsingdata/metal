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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.not;
import static nl.minvenj.nfi.metal.Shorthand.pre;
import static nl.minvenj.nfi.metal.Shorthand.ref;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.Shorthand.sub;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseGraph;
import nl.minvenj.nfi.metal.data.ParseItem;
import nl.minvenj.nfi.metal.data.ParseRef;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.data.ParseValue;
import nl.minvenj.nfi.metal.data.ParseValueList;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
                       pre(sub(this, ref("left")), not(eq(ref("left"), con(0)))),
                       def("right", con(1)),
                       pre(sub(this, ref("right")), not(eq(ref("right"), con(0))))).parse(scope, env, enc);
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
        checkStruct(graph, graph, offset);
    }

    private void checkStruct(final ParseGraph root, final ParseGraph graph, final long offset) {
        checkHeader(graph, offset);
        final ParseItem left = graph.tail.tail.head;
        Assert.assertTrue(left.isValue());
        final long leftOffset = ((ParseValue)left).asNumeric().longValue();
        if (leftOffset != 0) {
            final ParseItem leftItem = graph.tail.tail.tail.head;
            Assert.assertFalse(leftItem.isValue());
            if (leftItem.isGraph()) {
                checkStruct(root, (ParseGraph)leftItem, leftOffset);
            } else if (leftItem.isRef()) {
                checkHeader(((ParseRef)leftItem).resolve(root), leftOffset);
            }
        }
        final ParseItem right = leftOffset != 0 ? graph.tail.tail.tail.tail.head : graph.tail.tail.tail.head;
        Assert.assertTrue(right.isValue());
        final long rightOffset = ((ParseValue)right).asNumeric().longValue();
        if (rightOffset != 0) {
            final ParseItem rightItem = (leftOffset != 0 ? graph.tail.tail.tail.tail.tail.head : graph.tail.tail.tail.tail.head);
            Assert.assertFalse(rightItem.isValue());
            if (rightItem.isGraph()) {
                checkStruct(root, (ParseGraph)rightItem, rightOffset);
            } else if (rightItem.isRef()) {
                checkHeader(((ParseRef)rightItem).resolve(root), rightOffset);
            }
        }
    }

    private void checkHeader(final ParseGraph graph, final long offset) {
        final ParseItem head = graph.head;
        Assert.assertTrue(head.isValue());
        Assert.assertEquals(HEAD, ((ParseValue)head).asNumeric().intValue());
        Assert.assertEquals(offset, ((ParseValue)head).getOffset());
        final ParseItem nr = graph.tail.head;
        Assert.assertTrue(nr.isValue());
    }

    @Test
    public void checkRegularTreeFlat() {
        Assert.assertTrue(_regular.succeeded());
        final ParseValueList nrs = _regular.getEnvironment().order.getAll("nr");
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
