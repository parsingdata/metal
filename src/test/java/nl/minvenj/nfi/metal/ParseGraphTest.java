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
import nl.minvenj.nfi.metal.data.ParseList;
import nl.minvenj.nfi.metal.data.ParseValue;

public class ParseGraphTest {

    private final ParseGraph pg;
    private final ParseGraph pgc;
    private final ParseValue a;
    private final ParseValue b;
    private final ParseValue c;
    private final ParseValue d;

    public ParseGraphTest() {
        a = makeVal('a', 0L);
        b = makeVal('b', 2L);
        c = makeVal('c', 4L);
        d = makeVal('d', 6L);
        pg = makeSimpleGraph();
        pgc = makeCycleGraph();
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
            .add(a)        //  |   |  [a]
            .closeBranch() //  |   +---+
            .add(b)        //  |  [b]
            .closeBranch() //  +---+
            .add(c)        // [c]
            .add(d);       // [d]
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
    public void simple() {
        Assert.assertTrue(pg.head.isValue());
        Assert.assertEquals(d, pg.head.getValue());
        Assert.assertTrue(pg.tail.head.isValue());
        Assert.assertEquals(c, pg.tail.head.getValue());
        Assert.assertTrue(pg.tail.tail.head.isGraph());
        Assert.assertTrue(pg.tail.tail.head.getGraph().head.isValue());
        Assert.assertEquals(b, pg.tail.tail.head.getGraph().head.getValue());
        Assert.assertTrue(pg.tail.tail.head.getGraph().tail.head.isGraph());
        Assert.assertTrue(pg.tail.tail.head.getGraph().tail.head.getGraph().head.isValue());
        Assert.assertEquals(a, pg.tail.tail.head.getGraph().tail.head.getGraph().head.getValue());
        Assert.assertTrue(pg.tail.tail.head.getGraph().tail.head.getGraph().tail.head.isValue());
        Assert.assertEquals(d, pg.tail.tail.head.getGraph().tail.head.getGraph().tail.head.getValue());
        Assert.assertTrue(pg.tail.tail.head.getGraph().tail.tail.head.isValue());
        Assert.assertEquals(c, pg.tail.tail.head.getGraph().tail.tail.head.getValue());
        Assert.assertTrue(pg.tail.tail.tail.head.isValue());
        Assert.assertEquals(b, pg.tail.tail.tail.head.getValue());
        Assert.assertTrue(pg.tail.tail.tail.tail.head.isValue());
        Assert.assertEquals(a, pg.tail.tail.tail.tail.head.getValue());
    }

    @Test
    public void simpleFlatten() {
        final ParseList flat = pg.flatten();
        Assert.assertEquals(d, flat.head);
        Assert.assertEquals(c, flat.tail.head);
        Assert.assertEquals(b, flat.tail.tail.head);
        Assert.assertEquals(a, flat.tail.tail.tail.head);
        Assert.assertEquals(d, flat.tail.tail.tail.tail.head);
        Assert.assertEquals(c, flat.tail.tail.tail.tail.tail.head);
        Assert.assertEquals(b, flat.tail.tail.tail.tail.tail.tail.head);
        Assert.assertEquals(a, flat.tail.tail.tail.tail.tail.tail.tail.head);
    }

    private static ParseValue makeVal(final char n, final long o) {
        return new ParseValue("", Character.toString(n), o, new byte[] { (byte) n }, enc());
    }

}
