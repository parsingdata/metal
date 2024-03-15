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

import static java.util.function.Function.identity;

import static io.parsingdata.metal.Util.checkContainsNoNulls;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.ImmutableList.create;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link Token} that specifies a choice out of a list of tokens.
 *<p>
 * A Cho consists of a list of <code>tokens</code>. If none of the tokens
 * succeed, the Cho fails. If any token succeeds, the Cho succeeds. Precedence
 * is from left to right.
 */
public class Cho extends CycleToken {

    public final ImmutableList<Token> tokens;

    public Cho(final String name, final Encoding encoding, final Token token1, final Token token2, final Token... additionalTokens) {
        super(name, encoding);
        this.tokens = create(checkContainsNoNulls(additionalTokens, "additionalTokens"))
            .addHead(checkNotNull(token2, "token2"))
            .addHead(checkNotNull(token1, "token1"));
    }

    @Override
    protected Optional<ParseState> parseImpl(final Environment environment) {
        return iterate(environment.addBranch(this), tokens);
    }

    private Optional<ParseState> iterate(final Environment environment, final ImmutableList<Token> list) {
         return list.stream()
             .map(token -> token.parse(environment))
             .filter(Optional::isPresent)
             .findFirst()
             .flatMap(identity())
             .map(result -> result.closeBranch(this));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + tokens + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(tokens, ((Cho)obj).tokens);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(super.immutableHashCode(), tokens);
    }

}
