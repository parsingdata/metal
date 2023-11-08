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

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;

/**
 * A {@link Token} that specifies a postcondition for parsing a nested token.
 * <p>
 * A Post consists of a <code>predicate</code> (an {@link Expression}) and a
 * <code>token</code> (a {@link Token}). First the token is parsed. If parsing
 * succeeds, then <code>predicate</code> is evaluated. If it evaluates to
 * <code>true</code>, this token will succeed. In all other situations, parsing
 * this token fails.
 *
 * @see Expression
 */
public class Post extends CycleToken {

    public final Token token;
    public final Expression predicate;

    public Post(final String name, final Token token, final Expression predicate, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.predicate = checkNotNull(predicate, "predicate");
    }

    @Override
    protected Optional<ParseState> parseImpl(final Environment environment) {
        return token
            .parse(environment.addBranch(this))
            .map(nextParseState -> predicate.eval(nextParseState, environment.encoding) ? success(nextParseState.closeBranch(this)) : failure())
            .orElseGet(Util::failure);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + "," + predicate + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(token, ((Post)obj).token)
            && Objects.equals(predicate, ((Post)obj).predicate);
    }

    @Override
    public int cachingHashCode() {
        return Objects.hash(super.cachingHashCode(), token, predicate);
    }

}
