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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;

import java.util.function.Consumer;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ImmutablePair;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class Callbacks {

    public static final Callbacks NONE = new Callbacks(null, new ImmutableList<>());

    public final Callback genericCallback;
    public final ImmutableList<ImmutablePair<Token, Callback>> tokenCallbacks;

    private Callbacks(final Callback genericCallback, final ImmutableList<ImmutablePair<Token, Callback>> tokenCallbacks) {
        this.genericCallback = genericCallback;
        this.tokenCallbacks = checkNotNull(tokenCallbacks, "tokenCallbacks");
    }

    public static Callbacks create() {
        return NONE;
    }

    public Callbacks add(final Callback genericCallback) {
        return new Callbacks(genericCallback, tokenCallbacks);
    }

    public Callbacks add(final Token token, final Callback callback) {
        return new Callbacks(genericCallback, tokenCallbacks.addHead(new ImmutablePair<>(token, callback)));
    }

    public static Consumer<Callback> success(final Token token, final ParseState before, final ParseState after) {
        return callback -> callback.handleSuccess(token, before, after);
    }

    public static Consumer<Callback> failure(final Token token, final ParseState before) {
        return callback -> callback.handleFailure(token, before);
    }

    public void handle(final Token token, final Consumer<Callback> handler) {
        if (genericCallback != null) {
            handler.accept(genericCallback);
        }
        handleCallbacks(tokenCallbacks, token, handler).computeResult();
    }

    private Trampoline<Void> handleCallbacks(final ImmutableList<ImmutablePair<Token, Callback>> callbacks, final Token token, final Consumer<Callback> handler) {
        if (callbacks.isEmpty()) {
            return complete(() -> null);
        }
        if (callbacks.head().left.equals(token)) {
            handler.accept(callbacks.head().right);
        }
        return intermediate(() -> handleCallbacks(callbacks.tail(), token, handler));
    }

    @Override
    public String toString() {
        return (genericCallback == null ? "" : "generic: " + genericCallback + "; ") +
            (tokenCallbacks.isEmpty() ? "" : "token: " + tokenCallbacks);
    }

}
