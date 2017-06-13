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
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Base class for all Token implementations.
 * <p>
 * A Token is the basic building block of a parser. It denotes something that
 * can be parsed, either by reading from the input or composing other tokens.
 * <p>
 * Specifies the two fields that all tokens share: <code>name</code> (a
 * String) and <code>encoding</code> (an {@link Encoding}). A Token is parsed
 * by calling one of the <code>parse</code> methods. Parsing a Token succeeds
 * if the returned {@link Optional} contains an {@link Environment}, otherwise
 * parsing has failed.
 * <p>
 * The <code>name</code> field is used during parsing to construct a scope, by
 * concatenating it to the incoming <code>scope</code> argument as follows:
 * <pre>{@code
 * scope + SEPARATOR + name
 * }</pre>
 * The <code>encoding</code> may be <code>null</code>. If it is not, it
 * overrides outer encoding specifications and is passed to nested tokens
 * instead. As such it can itself be overridden by explicit specifications in
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

    public Optional<Environment> parse(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final Encoding activeEncoding = this.encoding != null ? this.encoding : encoding;
        final Optional<Environment> result = parseImpl(makeScope(checkNotNull(scope, "scope")), checkNotNull(environment, "environment"), activeEncoding);
        environment.callbacks.handle(this, environment, result);
        return result;
    }

    public Optional<Environment> parse(final Environment environment, final Encoding encoding) throws IOException {
        return parse(NO_NAME, environment, encoding);
    }

    protected abstract Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException;

    private String makeScope(final String scope) {
        return scope + (scope.isEmpty() || name.isEmpty() ? NO_NAME : SEPARATOR) + name;
    }

    public boolean isLocal() { return true; }

    public Token getCanonical(final Environment environment) { return this; }

    protected String makeNameFragment() {
        return name.isEmpty() ? NO_NAME : name + ",";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(name, ((Token)obj).name)
            && Objects.equals(encoding, ((Token)obj).encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, encoding);
    }

}
