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
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.FinalTrampoline;
import io.parsingdata.metal.IntermediateTrampoline;
import io.parsingdata.metal.Trampoline;

/**
 * A {@link Token} that specifies a token to be parsed inside the result of a
 * provided {@link ValueExpression}.
 * <p>
 * A Tie consists of a <code>token</code> (a {@link Token}) and a
 * <code>dataExpression</code> (a {@link ValueExpression}). First
 * <code>dataExpression</code> is evaluated. Then each value is used as an
 * input to parse the token in. Tie succeeds if all parses of
 * <code>token</code> in all results succeed. Tie fails if
 * <code>dataExpression</code> evaluates to a list of values that is either
 * empty or contains an invalid value.
 *
 * @see ValueExpression
 */
public class Tie extends Token {

    public final Token token;
    public final ValueExpression dataExpression;

    public Tie(final String name, final Token token, final ValueExpression dataExpression, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.dataExpression = checkNotNull(dataExpression, "dataExpression");
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ImmutableList<Optional<Value>> dataResult = dataExpression.eval(environment.order, encoding);
        if (dataResult.isEmpty()) {
            return failure();
        }
        return iterate(scope, dataResult, 0, environment, environment.addBranch(this), encoding).computeResult();
    }

    private Trampoline<Optional<Environment>> iterate(final String scope, final ImmutableList<Optional<Value>> values, final int index, final Environment returnEnvironment, final Environment environment, final Encoding encoding) throws IOException {
        if (!values.head.isPresent()) {
            return (FinalTrampoline<Optional<Environment>>) Util::failure;
        }
        final Optional<Environment> result = token.parse(scope, environment.source(dataExpression, index, environment, encoding), encoding);
        if (result.isPresent()) {
            if (values.tail.isEmpty()) { return (FinalTrampoline<Optional<Environment>>) () -> success(new Environment(result.get().closeBranch().order, returnEnvironment.source, returnEnvironment.offset, returnEnvironment.callbacks)); }
            return (IntermediateTrampoline<Optional<Environment>>) () -> iterate(scope, values.tail, index + 1, returnEnvironment, result.get(), encoding);
        }
        return (FinalTrampoline<Optional<Environment>>) Util::failure;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + dataExpression + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(token, ((Tie)obj).token)
            && Objects.equals(dataExpression, ((Tie)obj).dataExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, dataExpression);
    }

}
