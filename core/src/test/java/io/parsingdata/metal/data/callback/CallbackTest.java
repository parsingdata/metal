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
import static io.parsingdata.metal.data.selection.ByToken.getAllRoots;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class CallbackTest {

    private int successCount = 0;
    private int failureCount = 0;

    private void incrementSuccess() {
        successCount++;
    }

    private void incrementFailure() {
        failureCount++;
    }

    @Test
    public void testHandleCallback() throws IOException {
        final Token one = def("one", 1, eq(con(1)));
        final Token two = def("two", 1, eq(con(2)));
        final Token cho = cho("cho", one, two);
        final Token sequence = seq("seq", cho, one);
        final Callback callback = new BaseCallback() {

            @Override
            public void handleSuccess(final Token token, final Environment environment) {
                incrementSuccess();
            }

            @Override
            protected void handleFailure(Token token, Environment environment) {
                incrementFailure();
            }

        };
        final TokenCallbackList callbacks = TokenCallbackList
            .create(new TokenCallback(one, callback))
            .add(new TokenCallback(two, callback))
            .add(new TokenCallback(cho, callback))
            .add(new TokenCallback(sequence, callback));
        final Environment env = new Environment(new InMemoryByteStream(new byte[] { 2, 1 }), callbacks);
        assertTrue(sequence.parse(env, enc()).succeeded);
        assertEquals(4, successCount);
        assertEquals(1, failureCount);
    }

    private Token simpleSeq = seq(any("a"), any("b"));

    private Callback createCallback(final long... offsets) {
        return new BaseCallback() {

            private int count = 0;

            @Override
            protected void handleSuccess(Token token, Environment environment) {
                final ParseItemList roots = getAllRoots(environment.order, token);
                assertEquals(offsets[count++], roots.head.asGraph().tail.head.asValue().getOffset());
            }

            @Override
            protected void handleFailure(Token token, Environment environment) {}
        };
    }

    private ParseItemList reverse(final ParseItemList oldList, final ParseItemList newList) {
        if (oldList.isEmpty()) { return newList; }
        return reverse(oldList.tail, newList.add(oldList.head));
    }

    @Test
    public void testSimpleCallback() throws IOException {
        final TokenCallbackList callbacks = TokenCallbackList.create(new TokenCallback(simpleSeq, createCallback(0L)));
        final Environment env = new Environment(new InMemoryByteStream(new byte[] { 1, 2 }), callbacks);
        assertTrue(simpleSeq.parse(env, enc()).succeeded);
    }

    @Test
    public void testRepSimpleCallback() throws IOException {
        final TokenCallbackList callbacks = TokenCallbackList.create(new TokenCallback(simpleSeq, createCallback(0L, 2L)));
        final Environment env = new Environment(new InMemoryByteStream(new byte[] { 1, 2, 3, 4 }), callbacks);
        assertTrue(rep(simpleSeq).parse(env, enc()).succeeded);
    }

}
