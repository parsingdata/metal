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

import static io.parsingdata.metal.Util.checkContainsNoNulls;
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;
import static io.parsingdata.metal.data.ImmutableList.create;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.FinalTrampoline;
import io.parsingdata.metal.IntermediateTrampoline;
import io.parsingdata.metal.Trampoline;

/**
 * A {@link Token} that specifies a dependency between a list of tokens.
 * <p>
 * A Seq consists of a list of <code>tokens</code>. If one of the tokens
 * doesn't succeed, the Seq fails. If all of the tokens succeed, the Seq will
 * succeed. Order is from left to right.
 */
public class Seq extends Token {

    public final ImmutableList<Token> tokens;

    public Seq(final String name, final Encoding encoding, final Token... tokens) {
        super(name, encoding);
        this.tokens = create(checkContainsNoNulls(tokens, "tokens"));
        if (this.tokens.size < 2) { throw new IllegalArgumentException("At least two Tokens are required."); }
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final Optional<Environment> result = iterate(scope, Optional.of(environment.addBranch(this)), encoding, tokens).computeResult();
        if (result.isPresent()) {
            return success(result.get().closeBranch());
        }
        return failure();
    }

    private Trampoline<Optional<Environment>> iterate(final String scope, final Optional<Environment> environment, final Encoding encoding, final ImmutableList<Token> list) throws IOException {
        if (!environment.isPresent()) {
            return (FinalTrampoline<Optional<Environment>>) Util::failure;
        }
        if (list.isEmpty()) {
            return (FinalTrampoline<Optional<Environment>>) () -> success(environment.get());
        } else {
            return (IntermediateTrampoline<Optional<Environment>>) () -> iterate(scope, list.head.parse(scope, environment.get(), encoding), encoding, list.tail);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + tokens + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(tokens, ((Seq)obj).tokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tokens);
    }

}
