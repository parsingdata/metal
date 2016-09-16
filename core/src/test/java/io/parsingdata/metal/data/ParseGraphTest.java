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

package io.parsingdata.metal.data;

import io.parsingdata.metal.token.Token;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.data.ParseGraph.EMPTY;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ParseGraphTest {

    private final Token t = any("t");

    private final ParseGraph pg;
    private final ParseGraph pgc;
    private final ParseGraph pgl;
    private final ParseValue a;
    private final ParseValue b;
    private final ParseValue c;
    private final ParseValue d;
    private final ParseValue e;
    private final ParseValue f;
    private final ParseValue g;
    private final ParseValue h;

    public ParseGraphTest() {
        a = makeVal('a', 0L);
        b = makeVal('b', 2L);
        c = makeVal('c', 4L);
        d = makeVal('d', 6L);
        e = makeVal('e', 8L);
        f = makeVal('f', 10L);
        g = makeVal('g', 12L);
        h = makeVal('h', 14L);
        pg = makeSimpleGraph();
        pgc = makeCycleGraph();
        pgl = makeLongGraph();
    }

    private static ParseValue makeVal(final char n, final long o) {
        return new ParseValue(Character.toString(n), def(Character.toString(n), o), o, new byte[] { (byte) n }, enc());
    }

    private ParseGraph makeSimpleGraph() {
        return ParseGraph
            .EMPTY
            .add(a)        // [a]
            .add(b)        // [b]
            .addBranch(t)  //  +---+
            .add(c)        //  |  [c]
            .addBranch(t)  //  |   +---+
            .add(d)        //  |   |  [d]
            .add(e)        //  |   |  [e]
            .closeBranch() //  |   +---+
            .add(f)        //  |  [f]
            .closeBranch() //  +---+
            .add(g)        // [g]
            .add(h);       // [h]
    }

    @Test
    public void simple() {
        Assert.assertTrue(pg.head.isValue());
        assertEquals(h, pg.head);
        Assert.assertTrue(pg.tail.head.isValue());
        assertEquals(g, pg.tail.head);
        Assert.assertTrue(pg.tail.tail.head.isGraph());
        Assert.assertTrue(pg.tail.tail.head.asGraph().head.isValue());
        assertEquals(f, pg.tail.tail.head.asGraph().head);
        Assert.assertTrue(pg.tail.tail.head.asGraph().tail.head.isGraph());
        Assert.assertTrue(pg.tail.tail.head.asGraph().tail.head.asGraph().head.isValue());
        assertEquals(e, pg.tail.tail.head.asGraph().tail.head.asGraph().head);
        Assert.assertTrue(pg.tail.tail.head.asGraph().tail.head.asGraph().tail.head.isValue());
        assertEquals(d, pg.tail.tail.head.asGraph().tail.head.asGraph().tail.head);
        Assert.assertTrue(pg.tail.tail.head.asGraph().tail.tail.head.isValue());
        assertEquals(c, pg.tail.tail.head.asGraph().tail.tail.head);
        Assert.assertTrue(pg.tail.tail.tail.head.isValue());
        assertEquals(b, pg.tail.tail.tail.head);
        Assert.assertTrue(pg.tail.tail.tail.tail.head.isValue());
        assertEquals(a, pg.tail.tail.tail.tail.head);
    }

    private ParseGraph makeCycleGraph() {
        return ParseGraph
            .EMPTY
            .add(a)
            .addBranch(t)
            .add(b)
            .add(new ParseRef(a.getOffset(), sub(any("a"), con(a.getOffset()))))
            .closeBranch();
    }

    @Test
    public void cycle() {
        assertEquals(2, pgc.size);
        Assert.assertTrue(pgc.head.isGraph());
        Assert.assertTrue(pgc.head.asGraph().head.isRef());
        assertEquals(pgc, pgc.head.asGraph().head.asRef().resolve(pgc));
        Assert.assertTrue(pgc.head.asGraph().tail.head.isValue());
        assertEquals(b, pgc.head.asGraph().tail.head);
        Assert.assertTrue(pgc.tail.head.isValue());
        assertEquals(a, pgc.tail.head);
    }

    private ParseGraph makeLongGraph() {
        return ParseGraph
            .EMPTY
            .add(a)
            .addBranch(t)
            .addBranch(t)
            .add(b)
            .closeBranch()
            .addBranch(t)
            .closeBranch()
            .add(c)
            .addBranch(t)
            .add(d)
            .closeBranch()
            .closeBranch()
            .add(e)
            .addBranch(t)
            .add(f)
            .closeBranch();
    }

    @Test
    public void listGraphs() {
        final ParseGraphList list = pgl.getGraphs();
        assertEquals(6, list.size);
    }

    @Test
    public void firstValue() {
        Assert.assertTrue(pgl.containsValue());
        assertEquals(a, pgl.getLowestOffsetValue());
        assertEquals(f, pgl.head.asGraph().getLowestOffsetValue());
        assertEquals(a, pg.getLowestOffsetValue());
        assertEquals(c, pg.tail.tail.head.asGraph().getLowestOffsetValue());
        assertEquals(d, pg.tail.tail.head.asGraph().tail.head.asGraph().getLowestOffsetValue());
    }

    @Test
    public void testSimpleGetGraphAfter() {
        final ParseGraph graph = makeSimpleGraph();
        final ParseItem itemB = graph.tail.tail.tail.head;
        Assert.assertTrue(itemB.isValue());
        assertEquals(b, itemB);
        final ParseGraph subGraph = graph.getGraphAfter(itemB);
        Assert.assertTrue(subGraph.head.isValue());
        assertEquals(h, subGraph.head);
        Assert.assertTrue(subGraph.tail.head.isValue());
        assertEquals(g, subGraph.tail.head);
        Assert.assertTrue(subGraph.tail.tail.head.isGraph());
        Assert.assertTrue(subGraph.tail.tail.head.asGraph().head.isValue());
        assertEquals(f, subGraph.tail.tail.head.asGraph().head);
    }

    @Test
    public void testSimpleToString() {
        assertThat(pg.toString(), is("graph(h(0x68), graph(g(0x67), graph(graph(f(0x66), graph(graph(e(0x65), graph(d(0x64), graph(terminator:Def), false), false), graph(c(0x63), graph(terminator:Def), false), false), false), graph(b(0x62), graph(a(0x61), graph(EMPTY), false), false), false), false), false)"));
    }

    @Test
    public void testCycleToString() {
        assertThat(pgc.toString(), is("graph(graph(ref(@0), graph(b(0x62), graph(terminator:Def), false), false), graph(a(0x61), graph(EMPTY), false), false)"));
    }

    @Test
    public void testLongToString() {
        assertThat(pgl.toString(), is("graph(graph(f(0x66), graph(terminator:Def), false), graph(e(0x65), graph(graph(graph(d(0x64), graph(terminator:Def), false), graph(c(0x63), graph(graph(terminator:Def), graph(graph(b(0x62), graph(terminator:Def), false), graph(terminator:Def), false), false), false), false), graph(a(0x61), graph(EMPTY), false), false), false), false)"));
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNone() throws IOException {
        assertEquals("None", NONE.toString());
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("This placeholder may not be invoked.");
        NONE.parse(stream(), enc());
    }

    @Test
    public void testCloseNotBranched() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Cannot close branch that is not open.");
        EMPTY.closeBranch();
    }

    @Test
    public void testAsValue() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseGraph to ParseValue.");
        EMPTY.asValue();
    }

    @Test
    public void testAsRef() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseGraph to ParseRef.");
        EMPTY.asRef();
    }

    @Test
    public void testCurrent() {
        assertNull(EMPTY.current());
        assertNull(EMPTY.add(new ParseRef(0, NONE)).current());
        assertNull(EMPTY.addBranch(NONE).current());
    }

}
