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

import static org.junit.Assert.assertEquals;
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

import org.junit.Test;

import io.parsingdata.metal.SubStructTest;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class CallbackTest {

    private int successCount = 0;
    private int failureCount = 0;
    private int linkedListCount = 0;

    @Test
    public void testHandleCallback() throws IOException {
        final Token one = def("one", 1, eq(con(1)));
        final Token two = def("two", 1, eq(con(2)));
        final Token cho = cho("cho", one, two);
        final Token sequence = seq("seq", cho, one);
        final Callback callback = new BaseCallback() {

            @Override
            public void handleSuccess(final Token token, final Environment environment) {
                successCount++;
            }

            @Override
            protected void handleFailure(Token token, Environment environment) {
                failureCount++;
            }

        };
        final TokenCallbackList callbacks = TokenCallbackList
            .create(new TokenCallback(one, callback))
            .add(new TokenCallback(two, callback))
            .add(new TokenCallback(cho, callback))
            .add(new TokenCallback(sequence, callback));
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 2, 1 }), callbacks);
        assertTrue(sequence.parse(environment, enc()).succeeded);
        assertEquals(4, successCount);
        assertEquals(1, failureCount);
    }

    private static final Token SIMPLE_SEQ = seq(any("a"), any("b"));

    private TokenCallbackList createCallbackList(Token token, final long... offsets) {
        return TokenCallbackList.create(new TokenCallback(token, new BaseCallback() {

            private int count = 0;

            @Override
            protected void handleSuccess(Token token, Environment environment) {
                final ParseItemList roots = getAllRoots(environment.order, token);
                assertEquals(offsets[count++], roots.head.asGraph().tail.head.asValue().getOffset());
            }

            @Override
            protected void handleFailure(Token token, Environment environment) {}
        }));
    }

    @Test
    public void testSimpleCallback() throws IOException {
        final TokenCallbackList callbacks = createCallbackList(SIMPLE_SEQ, 0L);
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 1, 2 }), callbacks);
        assertTrue(SIMPLE_SEQ.parse(environment, enc()).succeeded);
    }

    @Test
    public void testRepSimpleCallback() throws IOException {
        final TokenCallbackList callbacks = createCallbackList(SIMPLE_SEQ, 0L, 2L);
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 1, 2, 3, 4 }), callbacks);
        assertTrue(rep(SIMPLE_SEQ).parse(environment, enc()).succeeded);
    }

    @Test
    public void seqAndRepCallbacks() throws IOException {
        final Token repeatingSeq = rep(SIMPLE_SEQ);
        final TokenCallbackList callbacks = createCallbackList(SIMPLE_SEQ, 0L, 2L)
                .add(new TokenCallback(repeatingSeq, new BaseCallback() {
                    @Override
                    protected void handleSuccess(Token token, Environment environment) {
                        final ParseItemList repRoots = getAllRoots(environment.order, token);
                        assertEquals(1, repRoots.size);

                        // verify that two Seq tokens were parsed:
                        final ParseItemList seqRoots = getAllRoots(environment.order, SIMPLE_SEQ);
                        assertEquals(2, seqRoots.size);

                        // verify order of the two Seq graphs:
                        assertEquals(2, getValue(seqRoots.head.asGraph(), "a").getOffset());
                        assertEquals(0, getValue(seqRoots.tail.head.asGraph(), "a").getOffset());
                    }

                    @Override
                    protected void handleFailure(Token token, Environment environment) {}
                }));
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 1, 2, 3, 4 }), callbacks);
        assertTrue(repeatingSeq.parse(environment, enc()).succeeded);
    }

    @Test
    public void refInCallback() throws IOException {
        TokenCallbackList callbacks = TokenCallbackList.create(new TokenCallback(SubStructTest.LINKED_LIST, new BaseCallback() {
            @Override
            protected void handleSuccess(Token token, Environment environment) {
                linkedListCount++;
            }

            @Override
            protected void handleFailure(Token token, Environment environment) {}
        }));
        final Environment environment = new Environment(new InMemoryByteStream(new byte[] { 0, 3, 1, 0, 0, 1 }), callbacks);
        assertTrue(SubStructTest.LINKED_LIST.parse(environment, enc()).succeeded);
        // The ParseReference does not trigger the callback:
        assertEquals(2, linkedListCount);
    }

}
