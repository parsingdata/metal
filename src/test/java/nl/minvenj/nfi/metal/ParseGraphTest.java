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

import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;

import org.junit.Assert;
import org.junit.Test;

import nl.minvenj.nfi.metal.data.ParseGraph;
import nl.minvenj.nfi.metal.data.ParseGraphList;
import nl.minvenj.nfi.metal.data.ParseItem;
import nl.minvenj.nfi.metal.data.ParseValue;

public class ParseGraphTest {

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
        return new ParseValue("", Character.toString(n), o, new byte[] { (byte) n }, enc());
    }

    private ParseGraph makeSimpleGraph() {
        return ParseGraph
            .EMPTY
            .add(a)        // [a]
            .add(b)        // [b]
            .addBranch()   //  +---+
            .add(c)        //  |  [c]
            .addBranch()   //  |   +---+
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
        Assert.assertEquals(h, pg.head.getValue());
        Assert.assertTrue(pg.tail.head.isValue());
        Assert.assertEquals(g, pg.tail.head.getValue());
        Assert.assertTrue(pg.tail.tail.head.isGraph());
        Assert.assertTrue(pg.tail.tail.head.getGraph().head.isValue());
        Assert.assertEquals(f, pg.tail.tail.head.getGraph().head.getValue());
        Assert.assertTrue(pg.tail.tail.head.getGraph().tail.head.isGraph());
        Assert.assertTrue(pg.tail.tail.head.getGraph().tail.head.getGraph().head.isValue());
        Assert.assertEquals(e, pg.tail.tail.head.getGraph().tail.head.getGraph().head.getValue());
        Assert.assertTrue(pg.tail.tail.head.getGraph().tail.head.getGraph().tail.head.isValue());
        Assert.assertEquals(d, pg.tail.tail.head.getGraph().tail.head.getGraph().tail.head.getValue());
        Assert.assertTrue(pg.tail.tail.head.getGraph().tail.tail.head.isValue());
        Assert.assertEquals(c, pg.tail.tail.head.getGraph().tail.tail.head.getValue());
        Assert.assertTrue(pg.tail.tail.tail.head.isValue());
        Assert.assertEquals(b, pg.tail.tail.tail.head.getValue());
        Assert.assertTrue(pg.tail.tail.tail.tail.head.isValue());
        Assert.assertEquals(a, pg.tail.tail.tail.tail.head.getValue());
    }

    private ParseGraph makeCycleGraph() {
        return ParseGraph
            .EMPTY
            .add(a)
            .addBranch()
            .add(b)
            .addRef(a.getOffset())
            .closeBranch();
    }

    @Test
    public void cycle() {
        Assert.assertEquals(2, pgc.size);
        Assert.assertTrue(pgc.head.isGraph());
        Assert.assertTrue(pgc.head.getGraph().head.isRef());
        Assert.assertEquals(pgc, pgc.head.getGraph().head.getRef().resolve(pgc));
        Assert.assertTrue(pgc.head.getGraph().tail.head.isValue());
        Assert.assertEquals(b, pgc.head.getGraph().tail.head.getValue());
        Assert.assertTrue(pgc.tail.head.isValue());
        Assert.assertEquals(a, pgc.tail.head.getValue());
    }

    private ParseGraph makeLongGraph() {
        return ParseGraph
            .EMPTY
            .add(a)
            .addBranch()
            .addBranch()
            .add(b)
            .closeBranch()
            .addBranch()
            .closeBranch()
            .add(c)
            .addBranch()
            .add(d)
            .closeBranch()
            .closeBranch()
            .add(e)
            .addBranch()
            .add(f)
            .closeBranch();
    }

    @Test
    public void listGraphs() {
        final ParseGraphList list = pgl.getGraphs();
        Assert.assertEquals(6, list.size);
    }

    @Test
    public void firstValue() {
        Assert.assertTrue(pgl.containsValue());
        Assert.assertEquals(a, pgl.getLowestOffsetValue());
        Assert.assertEquals(f, pgl.head.getGraph().getLowestOffsetValue());
        Assert.assertEquals(a, pg.getLowestOffsetValue());
        Assert.assertEquals(c, pg.tail.tail.head.getGraph().getLowestOffsetValue());
        Assert.assertEquals(d, pg.tail.tail.head.getGraph().tail.head.getGraph().getLowestOffsetValue());
    }

    @Test
    public void testSimpleGetGraphAfter() {
        final ParseGraph graph = makeSimpleGraph();
        final ParseItem itemB = graph.tail.tail.tail.head;
        Assert.assertTrue(itemB.isValue());
        Assert.assertEquals(b, itemB.getValue());
        final ParseGraph subGraph = graph.getGraphAfter(itemB);
        Assert.assertTrue(subGraph.head.isValue());
        Assert.assertEquals(h, subGraph.head.getValue());
        Assert.assertTrue(subGraph.tail.head.isValue());
        Assert.assertEquals(g, subGraph.tail.head.getValue());
        Assert.assertTrue(subGraph.tail.tail.head.isGraph());
        Assert.assertTrue(subGraph.tail.tail.head.getGraph().head.isValue());
        Assert.assertEquals(f, subGraph.tail.tail.head.getGraph().head.getValue());
    }

}
