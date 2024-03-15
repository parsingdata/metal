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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ZERO;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.ParseGraph.EMPTY;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.data.selection.ByTypeTest.EMPTY_SOURCE;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;

import org.junit.jupiter.api.Test;

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

    public ParseGraphTest() {
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
       Optional<ParseState> result = token.parse(env(stream(97, 0, 98, 0, 99, 0, 100, 0, 101, 0, 102, 0, 103, 0, 104, 0)));
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
            .add(new ParseReference(a.slice().offset, a.slice().source, aDef))
            .closeBranch();
    }

    @Test
    public void cycle() {
        assertEquals(2, pgc.size);
        assertTrue(pgc.head.isGraph());
        assertTrue(pgc.head.asGraph().head.isReference());
        assertEquals(a, pgc.head.asGraph().head.asReference().resolve(pgc).get());
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
    public void testSimpleToString() {
        assertThat(pg.toString(), is("pg(pval(h:0x68),pg(pval(g:0x67),pg(pg(pval(f:0x66),pg(pg(pval(e:0x65),pg(pval(d:0x64),pg(terminator:Def),false),false),pg(pval(c:0x63),pg(terminator:Def),false),false),false),pg(pval(b:0x62),pg(pval(a:0x61),pg(EMPTY),false),false),false),false),false)"));
    }

    @Test
    public void testCycleToString() {
        assertThat(pgc.toString(), is("pg(pg(pref(@0),pg(pval(b:0x62),pg(terminator:Def),false),false),pg(pval(a:0x61),pg(EMPTY),false),false)"));
    }

    @Test
    public void testLongToString() {
        assertThat(pgl.toString(), is("pg(pg(pval(f:0x66),pg(terminator:Def),false),pg(pval(e:0x65),pg(pg(pg(pval(d:0x64),pg(terminator:Def),false),pg(pval(c:0x63),pg(pg(terminator:Def),pg(pg(pval(b:0x62),pg(terminator:Def),false),pg(terminator:Def),false),false),false),false),pg(pval(a:0x61),pg(EMPTY),false),false),false),false)"));
    }

    @Test
    public void testNone() {
        assertEquals("None", NONE.toString());
        final Exception e = assertThrows(IllegalStateException.class, () -> NONE.parse(env(stream())));
        assertEquals("This placeholder may not be invoked.", e.getMessage());
    }

    @Test
    public void testCloseNotBranched() {
        final Exception e = assertThrows(IllegalStateException.class, EMPTY::closeBranch);
        assertEquals("Cannot close branch that is not open.", e.getMessage());
    }

    @Test
    public void testAsValue() {
        final Exception e = assertThrows(UnsupportedOperationException.class, EMPTY::asValue);
        assertEquals("Cannot convert to ParseValue.", e.getMessage());
    }

    @Test
    public void testAsRef() {
        final Exception e = assertThrows(UnsupportedOperationException.class, EMPTY::asReference);
        assertEquals("Cannot convert to ParseReference.", e.getMessage());
    }

    @Test
    public void testCurrent() {
        assertFalse(EMPTY.current().isPresent());
        assertFalse(EMPTY.add(new ParseReference(ZERO, EMPTY_SOURCE, NONE)).current().isPresent());
        assertFalse(EMPTY.addBranch(NONE).current().isPresent());
    }

}
