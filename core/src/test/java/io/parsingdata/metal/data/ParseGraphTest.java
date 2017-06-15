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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.nod;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.ParseGraph.EMPTY;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.data.selection.ByTypeTest.EMPTY_SOURCE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static junit.framework.TestCase.assertNull;

import java.io.IOException;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.token.Token;

public class ParseGraphTest {

    private final Token t = any("t");

    private final ParseGraph pg;
    private final ParseGraph pgc;
    private final ParseGraph pgl;
    private final Token aDef;
    private final ParseValue a;
    private final ParseValue b;
    private final ParseValue c;
    private final ParseValue d;
    private final ParseValue e;
    private final ParseValue f;
    private final ParseValue g;
    private final ParseValue h;

    public ParseGraphTest() throws IOException {
        aDef = any("a");
        Token token =
            seq(aDef, any("empty"),
                any("b"), any("empty"),
                any("c"), any("empty"),
                any("d"), any("empty"),
                any("e"), any("empty"),
                any("f"), any("empty"),
                any("g"), any("empty"),
                any("h"), any("empty")
            );
       Optional<Environment> result = token.parse(stream(97, 0, 98, 0, 99, 0, 100, 0, 101, 0, 102, 0, 103, 0, 104, 0), enc());
        a = getValue(result.get().order, "a");
        b = getValue(result.get().order, "b");
        c = getValue(result.get().order, "c");
        d = getValue(result.get().order, "d");
        e = getValue(result.get().order, "e");
        f = getValue(result.get().order, "f");
        g = getValue(result.get().order, "g");
        h = getValue(result.get().order, "h");
        pg = makeSimpleGraph();
        pgc = makeCycleGraph();
        pgl = makeLongGraph();
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
        assertTrue(pg.head.isValue());
        assertEquals(h, pg.head);
        assertTrue(pg.tail.head.isValue());
        assertEquals(g, pg.tail.head);
        assertTrue(pg.tail.tail.head.isGraph());
        assertTrue(pg.tail.tail.head.asGraph().head.isValue());
        assertEquals(f, pg.tail.tail.head.asGraph().head);
        assertTrue(pg.tail.tail.head.asGraph().tail.head.isGraph());
        assertTrue(pg.tail.tail.head.asGraph().tail.head.asGraph().head.isValue());
        assertEquals(e, pg.tail.tail.head.asGraph().tail.head.asGraph().head);
        assertTrue(pg.tail.tail.head.asGraph().tail.head.asGraph().tail.head.isValue());
        assertEquals(d, pg.tail.tail.head.asGraph().tail.head.asGraph().tail.head);
        assertTrue(pg.tail.tail.head.asGraph().tail.tail.head.isValue());
        assertEquals(c, pg.tail.tail.head.asGraph().tail.tail.head);
        assertTrue(pg.tail.tail.tail.head.isValue());
        assertEquals(b, pg.tail.tail.tail.head);
        assertTrue(pg.tail.tail.tail.tail.head.isValue());
        assertEquals(a, pg.tail.tail.tail.tail.head);
    }

    private ParseGraph makeCycleGraph() {
        return ParseGraph
            .EMPTY
            .add(a)
            .addBranch(t)
            .add(b)
            .add(new ParseReference(a.slice.offset, a.slice.source, aDef))
            .closeBranch();
    }

    @Test
    public void cycle() {
        assertEquals(2, pgc.size);
        assertTrue(pgc.head.isGraph());
        assertTrue(pgc.head.asGraph().head.isReference());
        assertEquals(a, pgc.head.asGraph().head.asReference().resolve(pgc));
        assertTrue(pgc.head.asGraph().tail.head.isValue());
        assertEquals(b, pgc.head.asGraph().tail.head);
        assertTrue(pgc.tail.head.isValue());
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
    public void testLong() {
        assertTrue(pgl.head.isGraph());
        assertTrue(pgl.head.asGraph().head.isValue());
        assertEquals(f, pgl.head.asGraph().head.asValue());
        assertTrue(pgl.tail.head.isValue());
        assertEquals(e, pgl.tail.head.asValue());
        assertTrue(pgl.tail.tail.head.isGraph());
        assertTrue(pgl.tail.tail.head.asGraph().head.isGraph());
        assertTrue(pgl.tail.tail.head.asGraph().head.asGraph().head.isValue());
        assertEquals(d, pgl.tail.tail.head.asGraph().head.asGraph().head.asValue());
        assertTrue(pgl.tail.tail.head.asGraph().tail.isGraph());
        assertTrue(pgl.tail.tail.head.asGraph().tail.asGraph().head.isValue());
        assertEquals(c, pgl.tail.tail.head.asGraph().tail.asGraph().head.asValue());
        assertTrue(pgl.tail.tail.head.asGraph().tail.tail.head.isGraph());
        assertTrue(pgl.tail.tail.head.asGraph().tail.tail.head.asGraph().isEmpty());
        assertTrue(pgl.tail.tail.head.asGraph().tail.tail.tail.head.isGraph());
        assertTrue(pgl.tail.tail.head.asGraph().tail.tail.tail.head.asGraph().head.isValue());
        assertEquals(b, pgl.tail.tail.head.asGraph().tail.tail.tail.head.asGraph().head.asValue());
        assertTrue(pgl.tail.tail.tail.head.isValue());
        assertEquals(a, pgl.tail.tail.tail.head.asValue());
    }

    @Test
    public void testHeadContainsLowestOffsetValue() throws IOException {
        final Environment environment = stream(0, 0, 0);
        final Token token = seq(
            repn(
                 def("zero", 1),
                 con(2)),
            nod(con(1)));
        // creates a ParseGraph with values in the head, and an empty graph as tail
        final Optional<Environment> result = token.parse(environment, enc());
        assertTrue(result.get().order.head.asGraph().head.asGraph().head.isValue());
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
        thrown.expectMessage("Cannot convert to ParseValue.");
        EMPTY.asValue();
    }

    @Test
    public void testAsRef() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert to ParseReference.");
        EMPTY.asReference();
    }

    @Test
    public void testCurrent() {
        assertNull(EMPTY.current());
        assertNull(EMPTY.add(new ParseReference(0, EMPTY_SOURCE, NONE)).current());
        assertNull(EMPTY.addBranch(NONE).current());
    }

}
