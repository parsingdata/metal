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

package io.parsingdata.metal.data.callback;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.data.selection.ByToken.getAllRoots;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.SubStructTest;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class CallbackTest {

    private static final Token ONE = def("one", 1, eq(con(1)));
    private static final Token TWO = def("two", 1, eq(con(2)));
    private static final Token THREE = def("three", 1, eq(con(3)));
    private static final Token FOUR = def("four", 1, eq(con(4)));
    private static final Token SEQ123 = seq(ONE, TWO, THREE);
    private static final Token SEQ124 = seq(ONE, TWO, FOUR);
    private static final Token CHOICE = cho(SEQ123, SEQ124);

    private long linkedListCount = 0;

    @Test
    public void testHandleCallback() throws IOException {
        final CountingCallback countingCallback = new CountingCallback();

        final Token cho = cho("cho", ONE, TWO);
        final Token sequence = seq("seq", cho, ONE);
        final Callbacks callbacks = Callbacks.create()
                .add(ONE, countingCallback)
                .add(TWO, countingCallback)
                .add(cho, countingCallback)
                .add(sequence, countingCallback);
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 2, 1 }), callbacks);
        assertTrue(sequence.parse(environment, enc()).isPresent());
        countingCallback.assertCounts(4, 1);
    }

    private static final Token SIMPLE_SEQ = seq(any("a"), any("b"));

    private Callbacks createCallbackList(Token token, final long... offsets) {
        return Callbacks.create().add(token, new BaseCallback() {

            private int count = 0;

            @Override
            protected void handleSuccess(Token token, Environment before, Environment after) {
                final ImmutableList<ParseItem> roots = getAllRoots(after.order, token);
                assertEquals(offsets[count++], roots.head.asGraph().tail.head.asValue().slice.offset);
            }

            @Override
            protected void handleFailure(Token token, Environment before) {}
        });
    }

    @Test
    public void testSimpleCallback() throws IOException {
        final Callbacks callbacks = createCallbackList(SIMPLE_SEQ, 0L);
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 1, 2 }), callbacks);
        assertTrue(SIMPLE_SEQ.parse(environment, enc()).isPresent());
    }

    @Test
    public void testRepSimpleCallback() throws IOException {
        final Callbacks callbacks = createCallbackList(SIMPLE_SEQ, 0L, 2L);
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 1, 2, 3, 4 }), callbacks);
        assertTrue(rep(SIMPLE_SEQ).parse(environment, enc()).isPresent());
    }

    @Test
    public void seqAndRepCallbacks() throws IOException {
        final Token repeatingSeq = rep(SIMPLE_SEQ);
        final Callbacks callbacks = createCallbackList(SIMPLE_SEQ, 0L, 2L)
                .add(repeatingSeq, new BaseCallback() {
                    @Override
                    protected void handleSuccess(Token token, Environment before, Environment after) {
                        final ImmutableList<ParseItem> repRoots = getAllRoots(after.order, token);
                        assertEquals(1, repRoots.size);

                        // verify that two Seq tokens were parsed:
                        final ImmutableList<ParseItem> seqRoots = getAllRoots(after.order, SIMPLE_SEQ);
                        assertEquals(2, seqRoots.size);

                        // verify order of the two Seq graphs:
                        assertEquals(2, getValue(seqRoots.head.asGraph(), "a").slice.offset);
                        assertEquals(0, getValue(seqRoots.tail.head.asGraph(), "a").slice.offset);
                    }

                    @Override
                    protected void handleFailure(Token token, Environment before) {}
                });
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 1, 2, 3, 4 }), callbacks);
        assertTrue(repeatingSeq.parse(environment, enc()).isPresent());
    }

    @Test
    public void refInCallback() throws IOException {
        final Callbacks callbacks = Callbacks.create().add(SubStructTest.LINKED_LIST, new BaseCallback() {
            @Override
            protected void handleSuccess(Token token, Environment before, Environment after) {
                linkedListCount++;
            }

            @Override
            protected void handleFailure(Token token, Environment before) {}
        });
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 0, 3, 1, 0, 0, 1 }), callbacks);
        assertTrue(SubStructTest.LINKED_LIST.parse(environment, enc()).isPresent());
        // The ParseReference does not trigger the callback:
        assertEquals(2, linkedListCount);
    }

    @Test
    public void genericCallback() throws IOException {
        final Deque<Token> expectedSuccessDefinitions = new ArrayDeque<>(Arrays.asList(ONE, TWO, ONE, TWO, FOUR, SEQ124, CHOICE));
        final Deque<Long> expectedSuccessOffsets = new ArrayDeque<>(Arrays.asList(1L, 2L, 1L, 2L, 3L, 3L, 3L));
        final Deque<Token> expectedFailureDefinitions = new ArrayDeque<>(Arrays.asList(THREE, SEQ123));
        final Deque<Long> expectedFailureOffsets = new ArrayDeque<>(Arrays.asList(2L, 0L));
        final OffsetDefinitionCallback genericCallback = new OffsetDefinitionCallback(
            expectedSuccessOffsets,
            expectedSuccessDefinitions,
            expectedFailureOffsets,
            expectedFailureDefinitions);

        final Callbacks callbacks = Callbacks.create().add(genericCallback);
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 1, 2, 4 }), callbacks);
        final Optional<Environment> parse = CHOICE.parse(environment, enc());
        assertTrue(parse.isPresent());
        genericCallback.assertAllHandled();
    }

    @Test
    public void tokenAndGenericCallbacks() throws IOException {
        final CountingCallback countingCallback = new CountingCallback();

        final Token cho = cho(ONE, TWO);

        final Deque<Token> expectedSuccessDefinitions = new ArrayDeque<>(Arrays.asList(TWO, cho));
        final Deque<Long> expectedSuccessOffsets = new ArrayDeque<>(Arrays.asList(1L, 1L));
        final Deque<Token> expectedFailureDefinitions = new ArrayDeque<>(Collections.singletonList(ONE));
        final Deque<Long> expectedFailureOffsets = new ArrayDeque<>(Collections.singletonList(0L));
        final OffsetDefinitionCallback genericCallback = new OffsetDefinitionCallback(
            expectedSuccessOffsets,
            expectedSuccessDefinitions,
            expectedFailureOffsets,
            expectedFailureDefinitions);

        final Callbacks callbacks = Callbacks.create()
            .add(genericCallback)
            .add(ONE, countingCallback)
            .add(TWO, countingCallback)
            .add(cho, countingCallback);
        final long expectedSuccessCount = expectedSuccessDefinitions.size();
        final long expectedFailureCount = expectedFailureDefinitions.size();
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 2 }), callbacks);
        assertTrue(cho.parse(environment, enc()).isPresent());
        genericCallback.assertAllHandled();
        countingCallback.assertCounts(expectedSuccessCount, expectedFailureCount);
    }

    private static class OffsetDefinitionCallback extends BaseCallback {
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
        protected void handleSuccess(Token token, Environment before, Environment after) {
            assertThat(after.offset, is(equalTo(expectedSuccessOffsets.pop())));
            assertThat(token, is(equalTo(expectedSuccessDefinitions.pop())));
        }

        @Override
        protected void handleFailure(Token token, Environment before) {
            assertThat(before.offset, is(equalTo(expectedFailureOffsets.pop())));
            assertThat(token, is(equalTo(expectedFailureDefinitions.pop())));
        }

        void assertAllHandled() {
            assertTrue(expectedSuccessOffsets.isEmpty());
            assertTrue(expectedSuccessDefinitions.isEmpty());
            assertTrue(expectedFailureOffsets.isEmpty());
            assertTrue(expectedFailureDefinitions.isEmpty());
        }
    }

    private class CountingCallback extends BaseCallback {
        private int successCount = 0;
        private int failureCount = 0;

        @Override
        public void handleSuccess(final Token token, Environment before, Environment after) {
            successCount++;
        }

        @Override
        protected void handleFailure(Token token, Environment before) {
            failureCount++;
        }

        void assertCounts(final long expectedSuccessCount, final long expectedFailureCount) {
            assertEquals(expectedSuccessCount, successCount);
            assertEquals(expectedFailureCount, failureCount);
        }
    }
}
