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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.token.Token;

public class Callbacks {

    public static final Callbacks NONE = new Callbacks(null, new ImmutableList<>());

    public final Callback genericCallback;
    public final ImmutableList<TokenCallback> tokenCallbacks;

    private Callbacks(final Callback genericCallback, final ImmutableList<TokenCallback> tokenCallbacks) {
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
        return new Callbacks(genericCallback, tokenCallbacks.add(new TokenCallback(token, callback)));
    }

    public void handle(final Token token, final Environment before, final Optional<Environment> after) {
        if (genericCallback != null) {
            genericCallback.handle(token, before, after);
        }
        handleCallbacks(tokenCallbacks, token, before, after).computeResult();
    }

    private Trampoline<Void> handleCallbacks(final ImmutableList<TokenCallback> callbacks, final Token token, final Environment before, final Optional<Environment> after) {
        if (callbacks.isEmpty()) {
            return complete(() -> null);
        }
        if (callbacks.head.token.equals(token)) {
            callbacks.head.callback.handle(token, before, after);
        }
        return intermediate(() -> handleCallbacks(callbacks.tail, token, before, after));
    }

    @Override
    public String toString() {
        return (genericCallback == null ? "" : "generic: " + genericCallback.toString() + "; ") +
                (tokenCallbacks.isEmpty() ? "" : "token: " + tokenCallbacks.toString());
    }

}
