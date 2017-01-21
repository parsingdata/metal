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

import static io.parsingdata.metal.Util.checkNotNull;

import java.util.function.BiConsumer;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;

public class Callbacks {

    public static final Callbacks NONE = new Callbacks(null, new ImmutableList<TokenCallback>());

    public final BiConsumer<Token, ParseResult> genericCallback;
    public final ImmutableList<TokenCallback> tokenCallbacks;

    private Callbacks(final BiConsumer<Token, ParseResult> genericCallback, final ImmutableList<TokenCallback> tokenCallbacks) {
        this.genericCallback = genericCallback;
        this.tokenCallbacks = checkNotNull(tokenCallbacks, "tokenCallbacks");
    }

    public static Callbacks create() { return NONE; }

    public Callbacks add(final BiConsumer<Token, ParseResult> genericCallback) {
        return new Callbacks(genericCallback, tokenCallbacks);
    }

    public Callbacks add(final Token token, final BiConsumer<Token, ParseResult> callback) {
        return new Callbacks(genericCallback, tokenCallbacks.add(new TokenCallback(token, callback)));
    }

    public void handle(final Token token, final ParseResult result) {
        if (genericCallback != null) {
            genericCallback.accept(token, result);
        }
        handleCallbacks(tokenCallbacks, token, result);
    }

    private void handleCallbacks(final ImmutableList<TokenCallback> callbacks, final Token token, final ParseResult result) {
        if (callbacks.isEmpty()) { return; }
        if (callbacks.head.token == token) {
            callbacks.head.callback.accept(token, result);
        }
        handleCallbacks(callbacks.tail, token, result);
    }

    @Override
    public String toString() {
        return (genericCallback == null ? "" : "generic: " + genericCallback.toString() + "; ") +
                (tokenCallbacks.isEmpty() ? "" : "token: " + tokenCallbacks.toString());
    }

}
