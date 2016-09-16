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
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
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

}
