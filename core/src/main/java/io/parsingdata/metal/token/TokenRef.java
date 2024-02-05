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

package io.parsingdata.metal.token;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.failure;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseState;
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
 */
public class TokenRef extends Token {

    private static final Token LOOKUP_FAILED = new Token("LOOKUP_FAILED", null) {
        @Override
        protected Optional<ParseState> parseImpl(final Environment environment) {
            return failure();
        }
    };

    public final String referenceName;

    public TokenRef(final String name, final String referenceName, final Encoding encoding) {
        super(name, encoding);
        this.referenceName = checkNotNull(referenceName, "referenceName");
        if (referenceName.isEmpty()) {
            throw new IllegalArgumentException("Argument referenceName may not be empty.");
        }
    }

    @Override
    protected Optional<ParseState> parseImpl(final Environment environment) {
        return lookup(ImmutableList.create(environment.parseState.order), referenceName).computeResult().parse(environment);
    }

    private Trampoline<Token> lookup(final ImmutableList<ParseItem> items, final String referenceName) {
        if (items.isEmpty()) {
            return complete(() -> LOOKUP_FAILED);
        }
        final ParseItem item = items.head();
        if (item.getDefinition().name.equals(referenceName)) {
            return complete(item::getDefinition);
        }
        if (item.isGraph() && !item.asGraph().isEmpty()) {
            return intermediate(() -> lookup(items.tail().addHead(item.asGraph().tail).addHead(item.asGraph().head), referenceName));
        }
        return intermediate(() -> lookup(items.tail(), referenceName));
    }

    @Override
    public Token getCanonical(final ParseState parseState) {
        return lookup(ImmutableList.create(parseState.order), referenceName).computeResult();
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
    public int immutableHashCode() {
        return Objects.hash(super.immutableHashCode(), referenceName);
    }

}
