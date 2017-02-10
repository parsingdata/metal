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
import static io.parsingdata.metal.data.ParseResult.failure;

import java.io.IOException;
import java.util.Objects;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link Token} that references a previously parsed token.
 * <p>
 * TokenRef consists of a <code>referenceName</code> (a String). In order to
 * allow the construction of recursive tokens, this token can be used to
 * reference an enclosing token and use it at the current location. An example
 * for use of this token is to recursively define a linked list.
 * <p>
 * The referenced token is located in the current parse state by traversing it
 * backwards until it is located. Parsing will fail if it is not found.
 *
 * @see io.parsingdata.metal.expression.value.reference.NameRef
 * @see io.parsingdata.metal.expression.value.reference.TokenRef
 */
public class TokenRef extends Token {

    private static final Token LOOKUP_FAILED = new Token("LOOKUP_FAILED", null) {
        @Override
        protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
            return failure(environment);
        }
    };

    public final String referenceName;

    public TokenRef(final String name, final String referenceName, final Encoding encoding) {
        super(name, encoding);
        this.referenceName = checkNotNull(referenceName, "referenceName");
        if (referenceName.isEmpty()) { throw new IllegalArgumentException("Argument referenceName may not be empty."); }
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        return lookup(environment.order, referenceName).parse(scope, environment, encoding);
    }

    private Token lookup(final ParseItem item, final String referenceName) {
        if (item.getDefinition().name.equals(referenceName)) {
            return item.getDefinition();
        }
        if (!item.isGraph() || item.asGraph().isEmpty()) { return LOOKUP_FAILED; }
        final Token headResult = lookup(item.asGraph().head, referenceName);
        if (headResult != LOOKUP_FAILED) { return headResult; }
        return lookup(item.asGraph().tail, referenceName);
    }

    @Override
    public Token getCanonical(final Environment environment) {
        return lookup(environment.order, referenceName);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + referenceName + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(referenceName, ((TokenRef)obj).referenceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), referenceName);
    }

}
