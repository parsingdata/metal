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

package io.parsingdata.metal.token;

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Base class for all Token implementations.
 *
 * Specifies the two fields that all tokens share: name (String) and encoding
 * ({@link Encoding}). A Token is parsed by calling one of the parse methods.
 *
 * The name field is used during parsing to construct a scope, by concatenating
 * the incoming scope as follows:
 * <pre>{@code
 * scope + SEPARATOR + name
 * }</pre>
 *
 * The encoding field may be null. If it is not null, it overrides outer
 * encoding specifications and is passed through to nested tokens instead. As
 * such it can itself be overridden by explicit specifications of encoding in
 * nested tokens.
 */
public abstract class Token {

    public static final String NO_NAME = "";
    public static final String SEPARATOR = ".";

    public final String name;
    public final Encoding encoding;

    protected Token(final String name, final Encoding encoding) {
        this.name = checkNotNull(name, "name");
        this.encoding = encoding;
    }

    public ParseResult parse(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final Encoding activeEncoding = this.encoding != null ? this.encoding : encoding;
        final ParseResult result = parseImpl(makeScope(scope), environment, activeEncoding);
        result.environment.callbacks.handle(this, result);
        return result;
    }

    public ParseResult parse(final Environment environment, final Encoding encoding) throws IOException {
        return parse(NO_NAME, environment, encoding);
    }

    protected abstract ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException;

    private String makeScope(final String scope) {
        return scope + (scope.isEmpty() || name.isEmpty() ? NO_NAME : SEPARATOR) + name;
    }

    public boolean isLocal() { return true; }

    public Token getCanonical(final Environment environment) { return this; }

    protected String makeNameFragment() {
        return name.isEmpty() ? NO_NAME : name + ",";
    }

}
