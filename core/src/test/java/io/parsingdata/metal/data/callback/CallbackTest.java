/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

package io.parsingdata.metal.data.callback;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.Selection.getAllRoots;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.SubStructTest;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class CallbackTest {

    private static final Token DEF_ONE = def("one", 1);
    private static final Token POST_ONE = post(DEF_ONE, eq(con(1)));
    private static final Token DEF_TWO = def("two", 1);
    private static final Token POST_TWO = post(DEF_TWO, eq(con(2)));
    private static final Token DEF_THREE = def("three", 1);
    private static final Token POST_THREE = post(DEF_THREE, eq(con(3)));
    private static final Token DEF_FOUR = def("four", 1);
    private static final Token POST_FOUR = post(DEF_FOUR, eq(con(4)));
    private static final Token SEQ123 = seq(POST_ONE, POST_TWO, POST_THREE);
    private static final Token SEQ124 = seq(POST_ONE, POST_TWO, POST_FOUR);
    private static final Token CHOICE = cho(SEQ123, SEQ124);

    private long linkedListCount = 0;

    private static final Token SIMPLE_SEQ = seq(any("a"), any("b"));

    @Test
    public void testHandleCallback() {
        final CountingCallback countingCallback = new CountingCallback();

        final Token cho = cho("cho", POST_ONE, POST_TWO);
        final Token sequence = seq("seq", cho, POST_ONE);
        final Callbacks callbacks = Callbacks.create()
                .add(POST_ONE, countingCallback)
                .add(POST_TWO, countingCallback)
                .add(cho, countingCallback)
                .add(sequence, countingCallback);
        final ParseState parseState = createFromByteStream(new InMemoryByteStream(new byte[] { 2, 1 }));
        assertTrue(sequence.parse(env(parseState, callbacks, enc())).isPresent());
        countingCallback.assertCounts(4, 1);
    }

    private Callbacks createCallbackList(Token token, final long... offsets) {
        return Callbacks.create().add(token, new Callback() {

            private int count = 0;

            @Override
            public void handleSuccess(Token token, ParseState before, ParseState after) {
                final ImmutableList<ParseItem> roots = getAllRoots(after.order, token);
                assertEquals(offsets[count++], roots.head().asGraph().tail.head.asValue().slice().offset.longValueExact());
            }

            @Override
            public void handleFailure(Token token, ParseState before) { /* empty */ }
        });
    }

    @Test
    public void testSimpleCallback() {
        final Callbacks callbacks = createCallbackList(SIMPLE_SEQ, 0L);
        final ParseState parseState = createFromByteStream(new InMemoryByteStream(new byte[] { 1, 2 }));
        assertTrue(SIMPLE_SEQ.parse(env(parseState, callbacks, enc())).isPresent());
    }

    @Test
    public void testRepSimpleCallback() {
        final Callbacks callbacks = createCallbackList(SIMPLE_SEQ, 0L, 2L);
        final ParseState parseState = createFromByteStream(new InMemoryByteStream(new byte[] { 1, 2, 3, 4 }));
        assertTrue(rep(SIMPLE_SEQ).parse(env(parseState, callbacks, enc())).isPresent());
    }

    @Test
    public void seqAndRepCallbacks() {
        final Token repeatingSeq = rep(SIMPLE_SEQ);
        final Callbacks callbacks = createCallbackList(SIMPLE_SEQ, 0L, 2L)
                .add(repeatingSeq, new Callback() {
                    @Override
                    public void handleSuccess(Token token, ParseState before, ParseState after) {
                        final ImmutableList<ParseItem> repRoots = getAllRoots(after.order, token);
                        assertEquals(1, (long) repRoots.size());

                        // verify that two Seq tokens were parsed:
                        final ImmutableList<ParseItem> seqRoots = getAllRoots(after.order, SIMPLE_SEQ);
                        assertEquals(2, (long) seqRoots.size());

                        // verify order of the two Seq graphs:
                        assertEquals(2, getValue(seqRoots.head().asGraph(), "a").slice().offset.intValueExact());
                        assertEquals(0, getValue(seqRoots.tail().head().asGraph(), "a").slice().offset.intValueExact());
                    }

                    @Override
                    public void handleFailure(Token token, ParseState before) { /* empty */ }
                });
        final ParseState parseState = createFromByteStream(new InMemoryByteStream(new byte[] { 1, 2, 3, 4 }));
        assertTrue(repeatingSeq.parse(env(parseState, callbacks, enc())).isPresent());
    }

    @Test
    public void refInCallback() {
        final Callbacks callbacks = Callbacks.create().add(SubStructTest.LINKED_LIST, new Callback() {
            @Override
            public void handleSuccess(Token token, ParseState before, ParseState after) {
                linkedListCount++;
            }

            @Override
            public void handleFailure(Token token, ParseState before) { /* empty */ }
        });
        final ParseState parseState = createFromByteStream(new InMemoryByteStream(new byte[] { 0, 3, 1, 0, 0, 1 }));
        assertTrue(SubStructTest.LINKED_LIST.parse(env(parseState, callbacks, enc())).isPresent());
        // The ParseReference does not trigger the callback:
        assertEquals(2, linkedListCount);
    }

    @Test
    public void genericCallback() {
        final Deque<Token> expectedSuccessDefinitions = new ArrayDeque<>(List.of(DEF_ONE, POST_ONE, DEF_TWO, POST_TWO, DEF_THREE, DEF_ONE, POST_ONE, DEF_TWO, POST_TWO, DEF_FOUR, POST_FOUR, SEQ124, CHOICE));
        final Deque<Long> expectedSuccessOffsets = new ArrayDeque<>(List.of(1L, 1L, 2L, 2L, 3L, 1L, 1L, 2L, 2L, 3L, 3L, 3L, 3L));
        final Deque<Token> expectedFailureDefinitions = new ArrayDeque<>(List.of(POST_THREE, SEQ123));
        final Deque<Long> expectedFailureOffsets = new ArrayDeque<>(List.of(2L, 0L));
        final OffsetDefinitionCallback genericCallback = new OffsetDefinitionCallback(
            expectedSuccessOffsets,
            expectedSuccessDefinitions,
            expectedFailureOffsets,
            expectedFailureDefinitions);

        final Callbacks callbacks = Callbacks.create().add(genericCallback);
        final ParseState parseState = createFromByteStream(new InMemoryByteStream(new byte[] { 1, 2, 4 }));
        final Optional<ParseState> parse = CHOICE.parse(env(parseState, callbacks, enc()));
        assertTrue(parse.isPresent());
        genericCallback.assertAllHandled();
    }

    @Test
    public void tokenAndGenericCallbacks() {
        final CountingCallback countingCallback = new CountingCallback();

        final Token cho = cho(POST_ONE, POST_TWO);

        final Deque<Token> expectedSuccessDefinitions = new ArrayDeque<>(List.of(DEF_ONE, DEF_TWO, POST_TWO, cho));
        final Deque<Long> expectedSuccessOffsets = new ArrayDeque<>(List.of(1L, 1L, 1L, 1L));
        final Deque<Token> expectedFailureDefinitions = new ArrayDeque<>(Collections.singletonList(POST_ONE));
        final Deque<Long> expectedFailureOffsets = new ArrayDeque<>(Collections.singletonList(0L));
        final OffsetDefinitionCallback genericCallback = new OffsetDefinitionCallback(
            expectedSuccessOffsets,
            expectedSuccessDefinitions,
            expectedFailureOffsets,
            expectedFailureDefinitions);

        final Callbacks callbacks = Callbacks.create()
            .add(genericCallback)
            .add(DEF_ONE, countingCallback)
            .add(POST_ONE, countingCallback)
            .add(DEF_TWO, countingCallback)
            .add(POST_TWO, countingCallback)
            .add(cho, countingCallback);
        final long expectedSuccessCount = expectedSuccessDefinitions.size();
        final long expectedFailureCount = expectedFailureDefinitions.size();
        final ParseState parseState = createFromByteStream(new InMemoryByteStream(new byte[] { 2 }));
        assertTrue(cho.parse(env(parseState, callbacks, enc())).isPresent());
        genericCallback.assertAllHandled();
        countingCallback.assertCounts(expectedSuccessCount, expectedFailureCount);
    }

    private static class OffsetDefinitionCallback implements Callback {
        private final Deque<Long> expectedSuccessOffsets;
        private final Deque<Token> expectedSuccessDefinitions;
        private final Deque<Long> expectedFailureOffsets;
        private final Deque<Token> expectedFailureDefinitions;

        OffsetDefinitionCallback(Deque<Long> expectedSuccessOffsets, Deque<Token> expectedSuccessDefinitions, Deque<Long> expectedFailureOffsets, Deque<Token> expectedFailureDefinitions) {
            this.expectedSuccessOffsets = expectedSuccessOffsets;
            this.expectedSuccessDefinitions = expectedSuccessDefinitions;
            this.expectedFailureOffsets = expectedFailureOffsets;
            this.expectedFailureDefinitions = expectedFailureDefinitions;
        }

        @Override
        public void handleSuccess(Token token, ParseState before, ParseState after) {
            assertThat(after.offset.longValueExact(), is(equalTo(expectedSuccessOffsets.pop())));
            assertThat(token, is(equalTo(expectedSuccessDefinitions.pop())));
        }

        @Override
        public void handleFailure(Token token, ParseState before) {
            assertThat(before.offset.longValueExact(), is(equalTo(expectedFailureOffsets.pop())));
            assertThat(token, is(equalTo(expectedFailureDefinitions.pop())));
        }

        public void assertAllHandled() {
            assertTrue(expectedSuccessOffsets.isEmpty());
            assertTrue(expectedSuccessDefinitions.isEmpty());
            assertTrue(expectedFailureOffsets.isEmpty());
            assertTrue(expectedFailureDefinitions.isEmpty());
        }
    }

    private static class CountingCallback implements Callback {
        private int successCount = 0;
        private int failureCount = 0;

        @Override
        public void handleSuccess(final Token token, ParseState before, ParseState after) {
            successCount++;
        }

        @Override
        public void handleFailure(Token token, ParseState before) {
            failureCount++;
        }

        public void assertCounts(final long expectedSuccessCount, final long expectedFailureCount) {
            assertEquals(expectedSuccessCount, successCount);
            assertEquals(expectedFailureCount, failureCount);
        }
    }
}
