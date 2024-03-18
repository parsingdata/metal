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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.scope;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.ParseGraph.EMPTY;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.data.selection.ByTypeTest.EMPTY_SOURCE;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.AutoEqualityTest;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ParseGraphTest {

    private static final Token t = any("t");
    private static final Token s = seq("scopeDelimiter", t, t);

    private static ParseGraph pg;
    private static ParseGraph pgc;
    private static ParseGraph pgl;
    private static List<ParseGraph> pgs;
    private static Token aDef;
    private static ParseValue a;
    private static ParseValue b;
    private static ParseValue c;
    private static ParseValue d;
    private static ParseValue e;
    private static ParseValue f;
    private static ParseValue g;
    private static ParseValue h;

    @BeforeAll
    public static void setup() {
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
        pgs = makeGraphList();
    }

    private static ParseGraph makeSimpleGraph() {
        return ParseGraph
            .EMPTY
            .add(a)         // [a]
            .add(b)         // [b]
            .addBranch(t)   //  +---+
            .add(c)         //  |  [c]
            .addBranch(t)   //  |   +---+
            .add(d)         //  |   |  [d]
            .add(e)         //  |   |  [e]
            .closeBranch(t) //  |   +---+
            .add(f)         //  |  [f]
            .closeBranch(t) //  +---+
            .add(g)         // [g]
            .add(h);        // [h]
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

    private static ParseGraph makeCycleGraph() {
        return ParseGraph
            .EMPTY
            .add(a)
            .addBranch(t)
            .add(b)
            .add(new ParseReference(a.slice().offset, a.slice().source, aDef))
            .closeBranch(t);
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

    private static ParseGraph makeLongGraph() {
        return ParseGraph
            .EMPTY
            .add(a)
            .addBranch(t)
            .addBranch(t)
            .add(b)
            .closeBranch(t)
            .addBranch(t)
            .closeBranch(t)
            .add(c)
            .addBranch(t)
            .add(d)
            .closeBranch(t)
            .closeBranch(t)
            .add(e)
            .addBranch(t)
            .add(f)
            .closeBranch(t);
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
        assertThat(pg.toString(), is("pg(pval(h:0x68),pg(pval(g:0x67),pg(pg(pval(f:0x66),pg(pg(pval(e:0x65),pg(pval(d:0x64),pg(terminator:Def),false,0),false,0),pg(pval(c:0x63),pg(terminator:Def),false,0),false,0),false,0),pg(pval(b:0x62),pg(pval(a:0x61),pg(EMPTY),false,0),false,0),false,0),false,0),false,0)"));
    }

    @Test
    public void testCycleToString() {
        assertThat(pgc.toString(), is("pg(pg(pref(@0),pg(pval(b:0x62),pg(terminator:Def),false,0),false,0),pg(pval(a:0x61),pg(EMPTY),false,0),false,0)"));
    }

    @Test
    public void testLongToString() {
        assertThat(pgl.toString(), is("pg(pg(pval(f:0x66),pg(terminator:Def),false,0),pg(pval(e:0x65),pg(pg(pg(pval(d:0x64),pg(terminator:Def),false,0),pg(pval(c:0x63),pg(pg(terminator:Def),pg(pg(pval(b:0x62),pg(terminator:Def),false,0),pg(terminator:Def),false,0),false,0),false,0),false,0),pg(pval(a:0x61),pg(EMPTY),false,0),false,0),false,0),false,0)"));
    }

    @Test
    public void testNone() {
        assertEquals("None", NONE.toString());
        final Exception e = assertThrows(IllegalStateException.class, () -> NONE.parse(env(stream())));
        assertEquals("This placeholder may not be invoked.", e.getMessage());
    }

    @Test
    public void testCloseNotBranched() {
        final Exception e = assertThrows(IllegalStateException.class, () -> EMPTY.closeBranch(t));
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

    private static List<ParseGraph> makeGraphList() {
        return List.of(
            EMPTY,
            EMPTY.add(a),
            EMPTY.add(b),
            EMPTY.add(a).add(b),
            EMPTY.add(b).add(a),

            EMPTY.addBranch(t),
            EMPTY.addBranch(s),
            EMPTY.add(a).addBranch(s),
            EMPTY.add(a).addBranch(t),
            EMPTY.add(b).addBranch(s),
            EMPTY.add(b).addBranch(t),
            EMPTY.add(a).addBranch(t).addBranch(s),
            EMPTY.add(a).addBranch(s).addBranch(t),
            EMPTY.add(b).addBranch(s).addBranch(t),
            EMPTY.add(b).addBranch(t).addBranch(s),

            EMPTY.add(a).addBranch(t).add(a),
            EMPTY.add(a).addBranch(t).add(b),
            EMPTY.add(b).addBranch(s).add(a),
            EMPTY.add(b).addBranch(t).add(b),
            EMPTY.add(a).addBranch(t).add(a).addBranch(s),
            EMPTY.add(a).addBranch(s).add(b).addBranch(t),
            EMPTY.add(b).addBranch(s).add(a).addBranch(t),
            EMPTY.add(b).addBranch(t).add(b).addBranch(s),

            EMPTY.add(a).addBranch(t).add(a).addBranch(s).add(a),
            EMPTY.add(a).addBranch(t).add(a).addBranch(s).add(b),
            EMPTY.add(a).addBranch(s).add(b).addBranch(t).add(a),
            EMPTY.add(a).addBranch(s).add(b).addBranch(t).add(b),
            EMPTY.add(b).addBranch(s).add(a).addBranch(t).add(a),
            EMPTY.add(b).addBranch(s).add(a).addBranch(t).add(b),
            EMPTY.add(b).addBranch(t).add(b).addBranch(s).add(a),
            EMPTY.add(b).addBranch(t).add(b).addBranch(s).add(b)
        );
    }

    public static Stream<Arguments> nonEqualityTest() {
        final List<Arguments> args = new ArrayList<>();
        for (int i = 0; i < pgs.size(); i++) {
            for (int j = 0; j < pgs.size(); j++) {
                if (i != j) {
                    args.add(arguments(i + "," + j, pgs.get(i), pgs.get(j)));
                }
            }
        }
        return args.stream();
    }

    @ParameterizedTest
    @MethodSource
    public void nonEqualityTest(final String testnr, final ParseGraph first, final ParseGraph second) {
        AutoEqualityTest.assertNotEquals(first, second);
        AutoEqualityTest.assertNotEquals(first.hashCode(), second.hashCode());
    }

    public static Stream<Arguments> equalityTest() {
        final List<Arguments> args = new ArrayList<>();
        for (int i = 0; i < pgs.size(); i++) {
            args.add(arguments(i, pgs.get(i), pgs.get(i)));
        }
        return args.stream();
    }

    @ParameterizedTest
    @MethodSource
    public void equalityTest(final int testnr, final ParseGraph first, final ParseGraph second) {
        AutoEqualityTest.assertEquals(first, second);
        AutoEqualityTest.assertEquals(first.hashCode(), second.hashCode());
    }

    public static Stream<Arguments> scopeDepthTest() {
        return Stream.of(
            // Add branches with and without scope delimited tokens.
            arguments(0, EMPTY),
            arguments(0, EMPTY.add(a)),
            arguments(0, EMPTY.addBranch(t)),
            arguments(1, EMPTY.addBranch(s)),
            arguments(1, EMPTY.addBranch(t).addBranch(s)),
            arguments(1, EMPTY.addBranch(s).addBranch(t)),
            arguments(2, EMPTY.addBranch(s).addBranch(s)),
            arguments(2, EMPTY.addBranch(s).addBranch(s).addBranch(t)),
            arguments(2, EMPTY.addBranch(s).addBranch(t).addBranch(s)),
            arguments(2, EMPTY.addBranch(t).addBranch(s).addBranch(s)),
            arguments(3, EMPTY.addBranch(s).addBranch(s).addBranch(s)),

            // Close branches with and without scope delimited tokens.
            arguments(2, EMPTY.addBranch(s).addBranch(s).addBranch(t).closeBranch(t)),
            arguments(1, EMPTY.addBranch(s).addBranch(t).addBranch(s).closeBranch(s)),
            arguments(2, EMPTY.addBranch(s).addBranch(s).addBranch(s).closeBranch(s)),
            arguments(0, EMPTY.addBranch(t).addBranch(s).addBranch(s).closeBranch(s).closeBranch(s)),
            arguments(1, EMPTY.addBranch(s).addBranch(s).addBranch(s).closeBranch(s).closeBranch(s)),
            arguments(0, EMPTY.addBranch(t).addBranch(s).addBranch(s).closeBranch(s).closeBranch(s).closeBranch(t)),
            arguments(0, EMPTY.addBranch(s).addBranch(s).addBranch(s).closeBranch(s).closeBranch(s).closeBranch(s)),

            // A previously closed branch should not interfere with the scopeDepth when adding branches.
            arguments(0, EMPTY.addBranch(t).closeBranch(t).addBranch(t)),
            arguments(1, EMPTY.addBranch(t).closeBranch(t).addBranch(s)),
            arguments(1, EMPTY.addBranch(t).closeBranch(t).addBranch(t).addBranch(s)),
            arguments(1, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(t)),
            arguments(2, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(s)),
            arguments(2, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(s).addBranch(t)),
            arguments(2, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(t).addBranch(s)),
            arguments(2, EMPTY.addBranch(t).closeBranch(t).addBranch(t).addBranch(s).addBranch(s)),
            arguments(3, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(s).addBranch(s)),

            // A previously closed branch should not interfere with the scopeDepth when closing branches.
            arguments(0, EMPTY.addBranch(t).closeBranch(t).addBranch(t)),
            arguments(0, EMPTY.addBranch(t).closeBranch(t).addBranch(s).closeBranch(s)),
            arguments(0, EMPTY.addBranch(t).closeBranch(t).addBranch(t).addBranch(s).closeBranch(s)),
            arguments(1, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(t).closeBranch(t)),
            arguments(1, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(s).closeBranch(s)),
            arguments(2, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(s).addBranch(t).closeBranch(t)),
            arguments(1, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(t).addBranch(s).closeBranch(s)),
            arguments(1, EMPTY.addBranch(t).closeBranch(t).addBranch(t).addBranch(s).addBranch(s).closeBranch(s)),
            arguments(2, EMPTY.addBranch(t).closeBranch(t).addBranch(s).addBranch(s).addBranch(s).closeBranch(s)),

            // Adding values should not interfere with the scopeDepth.
            arguments(0, EMPTY),
            arguments(0, EMPTY.add(a)),
            arguments(0, EMPTY.add(a).addBranch(t).add(a)),
            arguments(1, EMPTY.add(a).addBranch(s).add(a)),
            arguments(1, EMPTY.add(a).addBranch(t).add(a).addBranch(s).add(a)),
            arguments(1, EMPTY.add(a).addBranch(s).add(a).addBranch(t).add(a)),
            arguments(2, EMPTY.add(a).addBranch(s).add(a).addBranch(s).add(a)),
            arguments(2, EMPTY.add(a).addBranch(s).add(a).addBranch(s).add(a).addBranch(t).add(a)),
            arguments(2, EMPTY.add(a).addBranch(s).add(a).addBranch(t).add(a).addBranch(s).add(a)),
            arguments(2, EMPTY.add(a).addBranch(t).add(a).addBranch(s).add(a).addBranch(s).add(a)),
            arguments(3, EMPTY.add(a).addBranch(s).add(a).addBranch(s).add(a).addBranch(s).add(a)),
            arguments(0, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(t).add(a)),
            arguments(1, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(s).add(a)),
            arguments(1, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(t).add(a).addBranch(s).add(a)),
            arguments(1, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(s).add(a).addBranch(t).add(a)),
            arguments(2, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(s).add(a).addBranch(s).add(a)),
            arguments(2, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(s).add(a).addBranch(s).add(a).addBranch(t).add(a)),
            arguments(2, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(s).add(a).addBranch(t).add(a).addBranch(s).add(a)),
            arguments(2, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(t).add(a).addBranch(s).add(a).addBranch(s).add(a)),
            arguments(3, EMPTY.add(a).addBranch(t).add(a).closeBranch(t).add(a).addBranch(s).add(a).addBranch(s).add(a).addBranch(s).add(a))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void scopeDepthTest(final int scopeDepth, final ParseGraph graph) {
        assertEquals(scopeDepth, graph.scopeDepth);
    }

    public static Stream<Arguments> closeBranchExceptionTest() {
        final TestToken token = new TestToken("Test", null);
        return Stream.of(
            arguments("Cannot close branch that is not open.", EMPTY, null),
            arguments("Cannot close branch that is not open.", EMPTY.add(a), t),
            arguments("Cannot close branch that is not open.", EMPTY.addBranch(t).closeBranch(t), t),
            arguments("Cannot close branch that is not open.", EMPTY.addBranch(t).closeBranch(t), s),

            arguments("Cannot close branch with token that does not match its head token.", EMPTY.addBranch(t), s),
            arguments("Cannot close branch with token that does not match its head token.", EMPTY.addBranch(s), t),
            arguments("Cannot close branch with token that does not match its head token.", EMPTY.addBranch(t).addBranch(s), t),
            arguments("Cannot close branch with token that does not match its head token.", EMPTY.addBranch(s).addBranch(t), s),
            arguments("Cannot close branch with token that does not match its head token.", EMPTY.addBranch(t).addBranch(s).closeBranch(s), s),
            arguments("Cannot close branch with token that does not match its head token.", EMPTY.addBranch(s).addBranch(t).closeBranch(t), t),

            arguments("Cannot close parse graph that has a non zero scopeDepth.", EMPTY.addBranch(token.setScopeDelimiter(true)), token.setScopeDelimiter(false))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void closeBranchExceptionTest(final String errorMessage, final ParseGraph graph, final Token token) {
        final Exception e = assertThrows(IllegalStateException.class, () -> graph.closeBranch(token));
        assertEquals(errorMessage, e.getMessage());
    }

    private static class TestToken extends Token {

        private boolean isScopeDelimiter;

        protected TestToken(String name, Encoding encoding) {
            super(name, encoding);
        }

        @Override
        protected Optional<ParseState> parseImpl(Environment environment) {
            return Optional.empty();
        }
        @Override public String toString() { return "Test"; }

        @Override
        public boolean isScopeDelimiter() {
            return isScopeDelimiter;
        }
        public Token setScopeDelimiter(final boolean isScopeDelimiter) {
            this.isScopeDelimiter = isScopeDelimiter;
            return this;
        }
    }

}
