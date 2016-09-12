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

package io.parsingdata.metal.data;

import io.parsingdata.metal.data.callback.TokenCallbackList;
import io.parsingdata.metal.token.Token;

public class ParseResult {

    public final boolean succeeded;
    public final Environment environment;

    public ParseResult(final boolean succeeded, final Environment environment) {
        this.succeeded = succeeded;
        this.environment = environment;
    }

    public void handleCallbacks(final Token token) {
        handleCallbacks(environment.callbacks, token);
    }

    private void handleCallbacks(final TokenCallbackList callbacks, final Token token) {
        if (callbacks.isEmpty()) {
            return;
        }
        if (callbacks.head.token == token) {
            callbacks.head.callback.handle(token, this);
        }
        handleCallbacks(callbacks.tail, token);
    }

    public static ParseResult success(final Environment environment) {
        return new ParseResult(true, environment);
    }

    public static ParseResult failure(final Environment environment) {
        return new ParseResult(false, environment);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + succeeded + ", " + environment + ")";
    }

}
