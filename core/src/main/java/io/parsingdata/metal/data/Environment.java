/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.token.Token.NO_NAME;
import static io.parsingdata.metal.token.Token.SEPARATOR;

import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

public class Environment {

    public final String scope;
    public final ParseState parseState;
    public final Callbacks callbacks;
    public final Encoding encoding;

    public Environment(final String scope, final ParseState parseState, final Callbacks callbacks, final Encoding encoding) {
        this.scope = checkNotNull(scope, "scope");
        this.parseState = checkNotNull(parseState, "parseState");
        this.callbacks = checkNotNull(callbacks, "callbacks");
        this.encoding = checkNotNull(encoding, "encoding");
    }

    public Environment(final String scope, final ParseState parseState, final Encoding encoding) {
        this(scope, parseState, Callbacks.NONE, encoding);
    }

    public Environment(final ParseState parseState, final Callbacks callbacks, final Encoding encoding) {
        this(NO_NAME, parseState, callbacks, encoding);
    }

    public Environment(final ParseState parseState, final Encoding encoding) {
        this(parseState, Callbacks.NONE, encoding);
    }

    public Environment withParseState(final ParseState parseState) {
        return new Environment(scope, parseState, callbacks, encoding);
    }

    public Environment withEncoding(final Encoding encoding) {
        return new Environment(scope, parseState, callbacks, encoding);
    }

    public Environment addBranch(final Token token) {
        return withParseState(parseState.addBranch(token));
    }

    public Environment addCycleReference(final ParseReference parseReference) {
        return withParseState(parseState.add(parseReference));
    }

    public Environment extendScope(final String name) {
        return new Environment(scope + (scope.isEmpty() || name.isEmpty() ? NO_NAME : SEPARATOR) + name, parseState, callbacks, encoding);
    }

}
