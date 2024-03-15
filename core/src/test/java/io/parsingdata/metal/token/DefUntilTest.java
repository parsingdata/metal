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

package io.parsingdata.metal.token;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.CURRENT_OFFSET;
import static io.parsingdata.metal.Shorthand.EMPTY;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.join;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.until;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.callback.Callback;
import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.ValueExpression;

class DefUntilTest {

    private static final String INPUT_1 = "Hello, World!";
    private static final String INPUT_2 = "Another line...";
    private static final String INPUT_3 = "Another way to scroll...";
    private static final String INPUT = INPUT_1 + "\n" + INPUT_2 + "\n" + INPUT_3 + "\n";

    public static final Expression ENDS_WITH_NEWLINE = eq(mod(last(ref("line")), con(256)), con('\n'));
    public static final Token NEWLINE = def("newline", con(1), eq(con('\n')));
    public static final Token END_WITH_NEWLINE_POST = post(EMPTY, ENDS_WITH_NEWLINE);
    public static final Token END_WITH_NEWLINE_SUB = sub(NEWLINE, sub(CURRENT_OFFSET, con(1)));
    public static final Token NEXT_START_WITH_TERMINATOR = sub(NEWLINE, CURRENT_OFFSET);

    static Collection<Object[]> repTest() {
        return List.of(new Object[][] {
            { "until: terminator not part of line, available in parseGraph",  until("line", NEWLINE),                            3, 3, INPUT_1, INPUT_2, INPUT_3},
            { "until: terminator part of line, not available in parseGraph",  until("line", con(1), END_WITH_NEWLINE_POST),      3, 0, INPUT_1 + '\n', INPUT_2 + '\n', INPUT_3 + '\n'},
            { "until: terminator part of line, available in parseGraph",      until("line", con(1), END_WITH_NEWLINE_SUB),       3, 3, INPUT_1 + '\n', INPUT_2 + '\n', INPUT_3 + '\n'},
            { "until: terminator part of next line, available in parseGraph", until("line", con(1), NEXT_START_WITH_TERMINATOR), 3, 3, INPUT_1, '\n' + INPUT_2, '\n' + INPUT_3},

            { "def: terminator part of line, not available in parseGraph",      def("line", con(1), END_WITH_NEWLINE_POST),      3, 0, INPUT_1 + '\n', INPUT_2 + '\n', INPUT_3 + '\n'},
            { "def: terminator part of line, not available in parseGraph",      def("line", con(1), END_WITH_NEWLINE_SUB),       3, 0, INPUT_1 + '\n', INPUT_2 + '\n', INPUT_3 + '\n'},
            { "def: terminator part of line, not available in parseGraph",      def("line", ENDS_WITH_NEWLINE),                  3, 0, INPUT_1 + '\n', INPUT_2 + '\n', INPUT_3 + '\n'},
            { "def: terminator part of next line, not available in parseGraph", def("line", con(1), NEWLINE),                    3, 0, INPUT_1, '\n' + INPUT_2, '\n' + INPUT_3},
            { "def: terminator part of next line, not available in parseGraph", def("line", con(1), NEXT_START_WITH_TERMINATOR), 3, 0, INPUT_1, '\n' + INPUT_2, '\n' + INPUT_3},

            { "def: terminator not part of line, available in parseGraph",  seq(def("line", NEWLINE), NEWLINE),                                               3, 3, INPUT_1, INPUT_2, INPUT_3},
            { "def: terminator part of line, available in parseGraph",      seq(def("line", con(1), END_WITH_NEWLINE_SUB), END_WITH_NEWLINE_SUB),             3, 3, INPUT_1 + '\n', INPUT_2 + '\n', INPUT_3 + '\n'},
            { "def: terminator part of next line, available in parseGraph", seq(def("line", con(1), NEXT_START_WITH_TERMINATOR), NEXT_START_WITH_TERMINATOR), 3, 3, INPUT_1, '\n' + INPUT_2, '\n' + INPUT_3}
        });
    }

    @ParameterizedTest(name="{0}")
    @MethodSource
    void repTest(final String name, final Token token, final int lineCount, final int newlineCount, final String line1, final String line2, final String line3) {
        final Optional<ParseState> parseState = rep(token).parse(env(stream(INPUT, US_ASCII)));
        assertTrue(parseState.isPresent());

        ImmutableList<ParseValue> values = getAllValues(parseState.get().order, "line");
        assertEquals(lineCount, values.size);
        assertEquals(line1, values.tail.tail.head.asString());
        assertEquals(line2, values.tail.head.asString());
        assertEquals(line3, values.head.asString());

        ImmutableList<ParseValue> newLines = getAllValues(parseState.get().order, "newline");
        assertEquals(newlineCount, newLines.size);
    }

    static Collection<Object[]> shorthandTokenTest() {
        return List.of(new Object[][] {
            { "def",                         def("line", NEWLINE)               },
            { "def initial size",            def("line", con(13), NEWLINE)},
            { "def initial size + encoding", def("line", con(13), NEWLINE, Encoding.DEFAULT_ENCODING)},
            { "def step size",               def("line", con(13), con(1), NEWLINE)},
            { "def step size + encoding",    def("line", con(13), con(1), NEWLINE, Encoding.DEFAULT_ENCODING)},
            { "def max size",                def("line", con(13), con(1), con(24), NEWLINE)},
            { "def max size + encoding",     def("line", con(13), con(1), con(24), NEWLINE, Encoding.DEFAULT_ENCODING)},
        });
    }

    @ParameterizedTest(name="{0}")
    @MethodSource
    void shorthandTokenTest(final String name, final Token token) {
        final Optional<ParseState> parseState = rep(seq(token, NEWLINE)).parse(env(stream(INPUT, US_ASCII)));
        assertTrue(parseState.isPresent());

        ImmutableList<ParseValue> values = getAllValues(parseState.get().order, "line");
        assertEquals(3, values.size);
        assertEquals(INPUT_1, values.tail.tail.head.asString());
        assertEquals(INPUT_2, values.tail.head.asString());
        assertEquals(INPUT_3, values.head.asString());

        ImmutableList<ParseValue> newLines = getAllValues(parseState.get().order, "newline");
        assertEquals(3, newLines.size);
    }

    static Collection<Object[]> shorthandExpressionTest() {
        return List.of(new Object[][] {
            { "def token",                   def("line", con(1), END_WITH_NEWLINE_POST)},
            { "def expression",              def("line", ENDS_WITH_NEWLINE)},
            { "def expression + encoding",   def("line", ENDS_WITH_NEWLINE, Encoding.DEFAULT_ENCODING)},
        });
    }

    @ParameterizedTest(name="{0}")
    @MethodSource
    void shorthandExpressionTest(final String name, final Token token) {
        final Optional<ParseState> parseState = rep(token).parse(env(stream(INPUT, US_ASCII)));
        assertTrue(parseState.isPresent());

        ImmutableList<ParseValue> values = getAllValues(parseState.get().order, "line");
        assertEquals(3, values.size);
        assertEquals(INPUT_1 + "\n", values.tail.tail.head.asString());
        assertEquals(INPUT_2 + "\n", values.tail.head.asString());
        assertEquals(INPUT_3 + "\n", values.head.asString());

        ImmutableList<ParseValue> newLines = getAllValues(parseState.get().order, "newline");
        assertEquals(0, newLines.size);
    }

    @Test
    void allDefaultValueExpressions() {
        assertTrue(until("value", def("terminator", 1, eq(con(0)))).parse(env(stream(1, 2, 3, 0))).isPresent());
    }

    @Test
    void errorNegativeSize() {
        assertFalse(until("value", con(-1, signed()), def("terminator", 1, eq(con(0)))).parse(env(stream(1, 2, 3, 0))).isPresent());
    }

    @Test
    void nameScopeWithUntil() {
        assertNameScope(terminator -> until("value", terminator), 2);
    }

    @Test
    void nameScopeWithDef() {
        assertNameScope(terminator -> def("value", terminator), 1);
    }

    private static void assertNameScope(final Function<Token, Token> tokenProvider, int terminatorCount) {
        final Token terminator = def("terminator", con(1), eq(con(0x00)));
        final Token token = seq("struct", tokenProvider.apply(terminator), terminator);
        final Optional<ParseState> parse = token.parse(env(stream('d', 'a', 't', 'a', 0, 0)));
        assertTrue(parse.isPresent());
        assertEquals(terminatorCount, getAllValues(parse.get().order, "struct.terminator").size);
        assertEquals(1, getAllValues(parse.get().order, "struct.value").size);
        assertEquals("data", getAllValues(parse.get().order, "struct.value").head.asString());
    }

    @Test
    void listsSemanticsTest() {
        // It is ok for the lists to have different lengths. They are used simultaneously as triples, and the number of triples considered is equal to the length of the smallest list.
        final ValueExpression initialSize = join(con(12), con(7), con(5), con(0)); // 12 is ignored
        final ValueExpression stepSize = join(con(1), con(5), con(6));
        final ValueExpression maxSize = join(con(105), con(21), con(18), con(15), con(13)); // 21 and 105 are ignored
        final Token def = def("test", initialSize, stepSize, maxSize, def("terminator", 1, eq(con("X"))));

        // The above DefUntil token should check the following offsets:
        // combination 0-6-13: 0+0=0, 0+6=6, 0+12=12
        // combination 5-5-15: 5+0=5, 5+5=10, 5+10=15
        // combination 7-1-18: 7+0=7, 7+1=8, 7+2=9, 7+3=10, 7+4=11
        // Total checks expected = 11.

        final List<Integer> offsets = new ArrayList<>();
        final Callbacks callbacks = Callbacks.create().add(genericCallback(offsets, "terminator"));

        final Optional<ParseState> parse = def.parse(new Environment(stream("01234567890X--------", UTF_8), callbacks, enc()));
        assertTrue(parse.isPresent());
        assertAll(
            () -> assertEquals(11, parse.get().offset.intValue()),
            () -> assertEquals(11, offsets.size()),
            () -> assertIterableEquals(List.of(0, 6, 12, 5, 10, 15, 7, 8, 9, 10, 11), offsets),
            () -> assertEquals("01234567890", getAllValues(parse.get().order, "test").head.asString())
        );
    }

    private static Callback genericCallback(final List<Integer> checkCount, final String tokenName) {
        return new Callback() {
            @Override
            public void handleSuccess(Token token, ParseState before, ParseState after) {
                countTerminator(token, before);
            }

            @Override
            public void handleFailure(Token token, ParseState before) {
                countTerminator(token, before);
            }

            private void countTerminator(Token token, ParseState before) {
                if (token.name.equals(tokenName)) {
                    checkCount.add(before.offset.intValue());
                }
            }
        };
    }

}
