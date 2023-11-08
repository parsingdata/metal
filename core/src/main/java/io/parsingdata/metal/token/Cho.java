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
import static io.parsingdata.metal.Util.checkContainsNoNulls;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.success;
import static io.parsingdata.metal.data.ImmutableList.create;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
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
            .add(checkNotNull(token2, "token2"))
            .add(checkNotNull(token1, "token1"));
    }

    @Override
    protected Optional<ParseState> parseImpl(final Environment environment) {
        return iterate(environment.addBranch(this), tokens).computeResult();
    }

    private Trampoline<Optional<ParseState>> iterate(final Environment environment, final ImmutableList<Token> list) {
        if (list.isEmpty()) {
            return complete(Util::failure);
        }
        return list.head
            .parse(environment)
            .map(result -> complete(() -> success(result.closeBranch(this))))
            .orElseGet(() -> intermediate(() -> iterate(environment, list.tail)));
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
    public int cachingHashCode() {
        return Objects.hash(super.cachingHashCode(), tokens);
    }

}
