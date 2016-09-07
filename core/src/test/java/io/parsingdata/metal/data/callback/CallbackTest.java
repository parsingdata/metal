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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;
import org.junit.Test;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static org.junit.Assert.assertTrue;

public class CallbackTest {

    @Test
    public void testHandleCallback() throws IOException {
        final Token token = any("a");
        final Token sequence = seq(token, token);
        final Callback callback = new Callback() {
            @Override
            public void handle(final Token token, final ParseResult result, final Encoding enc) {
                System.out.print("Handled: ");
                final ParseItem item = result.environment.order.get(token);
                if (item.isGraph()) {
                    System.out.println("Graph: " + item.asGraph());
                } else if (item.isRef()) {
                    System.out.println("Ref: " + item.asRef());
                } else if (item.isValue()) {
                    System.out.println("Value: " + item.asValue());
                }
            }
        };
        final TokenCallbackList callbacks = TokenCallbackList.create(new TokenCallback(token, callback)).add(new TokenCallback(sequence, callback));
        final Environment env = new Environment(new InMemoryByteStream(new byte[] { 0, 0 }), callbacks);
        assertTrue(sequence.parse(env, enc()).succeeded);
    }

}
