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

import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.data.Environment;
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
public class Post extends Token {

    public final Token token;
    public final Expression predicate;

    public Post(final String name, final Token token, final Expression predicate, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.predicate = predicate == null ? expTrue() : predicate;
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final Optional<Environment> result = token.parse(scope, environment.addBranch(this), encoding);
        if (result.isPresent()) {
            final Environment newEnvironment = result.get().closeBranch();
            return predicate.eval(newEnvironment.order, encoding) ? success(newEnvironment) : failure();
        }
        return failure();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + predicate + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(token, ((Post)obj).token)
            && Objects.equals(predicate, ((Post)obj).predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, predicate);
    }

}
