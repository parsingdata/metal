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

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.util.FinalTrampoline;
import io.parsingdata.metal.token.util.IntermediateTrampoline;
import io.parsingdata.metal.token.util.Trampoline;

/**
 * A {@link Token} that specifies a conditional repetition of a token.
 * <p>
 * A While consists of a <code>token</code> (a {@link Token}) and a
 * <code>predicate</code> (an {@link Expression}). Each loop,
 * <code>predicate</code> is evaluated. If it evaluates to <code>true</code>,
 * <code>token</code> is parsed. If that succeeds, the loop is repeated. When
 * <code>predicate</code> evaluates to <code>false</code>, the While
 * terminates and succeeds. If parsing <code>token</code> fails, the While
 * also fails.
 *
 * @see Expression
 */
public class While extends Token {

    public final Token token;
    public final Expression predicate;

    public While(final String name, final Token token, final Expression predicate, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.predicate = predicate == null ? expTrue() : predicate;
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final Optional<Environment> result = iterate(scope, Optional.of(environment.addBranch(this)), encoding).computeResult();
        if (result.isPresent()) {
            return success(result.get().closeBranch());
        }
        return failure();
    }

    private Trampoline<Optional<Environment>> iterate(final String scope, final Optional<Environment> environment, final Encoding encoding) {
        if (!environment.isPresent()) {
            return (FinalTrampoline<Optional<Environment>>) Util::failure;
        }
        if (predicate.eval(environment.get().order, encoding)) {
            return (IntermediateTrampoline<Optional<Environment>>) () -> iterate(scope, token.parse(scope, environment.get(), encoding), encoding);
        } else {
            return (FinalTrampoline<Optional<Environment>>) () -> success(environment.get());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + predicate + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(token, ((While)obj).token)
            && Objects.equals(predicate, ((While)obj).predicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, predicate);
    }

}
