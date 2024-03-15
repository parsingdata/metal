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

package io.parsingdata.metal.token;

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.callback.Callbacks.failure;
import static io.parsingdata.metal.data.callback.Callbacks.success;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.reference.CurrentIteration;
import io.parsingdata.metal.expression.value.reference.Ref;

/**
 * Base class for all Token implementations.
 * <p>
 * A Token is the basic building block of a parser. It denotes something that
 * can be parsed, either by reading from the input or composing other tokens.
 * <p>
 * Specifies the two fields that all tokens share: <code>name</code> (a
 * String) and <code>encoding</code> (an {@link Encoding}). A Token is parsed
 * by calling one of the <code>parse</code> methods. Parsing a Token succeeds
 * if the returned {@link Optional} contains an {@link ParseState}, otherwise
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
public abstract class Token extends ImmutableObject {

    public static final String NO_NAME = "";
    public static final String SEPARATOR = ".";
    public static final String EMPTY_NAME = "__EMPTY__";

    public final String name;
    public final Encoding encoding;

    protected Token(final String name, final Encoding encoding) {
        this.name = checkNotNull(name, "name");
        this.encoding = encoding;
    }

    public Optional<ParseState> parse(final Environment environment) {
        final Environment activeEnvironment = this.encoding != null ? environment.withEncoding(this.encoding) : environment;
        final Optional<ParseState> result = parseImpl(activeEnvironment.extendScope(name));
        environment.callbacks.handle(this, result
            .map(after -> success(this, environment.parseState, after))
            .orElseGet(() -> failure(this, environment.parseState)));
        return result;
    }

    protected abstract Optional<ParseState> parseImpl(final Environment environment);

    /**
     * The {@link Ref} ValueExpression uses this property to determine which
     * part of the {@link ParseState}'s <code>order</code> (the {@link
     * ParseGraph}) field is considered to be in scope. The Tokens considered
     * to be scope delimiters are {@link Seq} and subclasses of {@link
     * IterableToken}.
     * @return a boolean that specifies whether this Token is a scope delimiter
     */
    public boolean isScopeDelimiter() {
        return false;
    }

    /**
     * The {@link Sub} Token uses this property as part of its cycle detection
     * algorithm. The only non-local Token is {@link Sub} itself, since it
     * parses not at the current but at a calculated location.
     * @return a boolean that specifies whether this Token is local
     */
    public boolean isLocal() {
        return true;
    }

    /**
     * The {@link CurrentIteration} ValueExpression uses this property to
     * determine which iteration is requested in its operand. Iterable Tokens
     * are all subclasses of {@link IterableToken}.
     * @return a boolean that specifies whether this Token is iterable
     */
    public boolean isIterable() {
        return false;
    }

    /**
     * Returns the Token it represents. In the case of {@link TokenRef}, this
     * means the Token it refers to instead of the TokenRef itself.
     * @param parseState the current {@link ParseState}
     * @return the Token represented by this Token
     */
    public Token getCanonical(final ParseState parseState) {
        return this;
    }

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
    public int immutableHashCode() {
        return Objects.hash(getClass(), name, encoding);
    }

}
