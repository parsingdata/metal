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
import static io.parsingdata.metal.data.ParseResult.success;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link Token} that specifies a token to be parsed inside the result of a
 * provided {@link ValueExpression}.
 *
 * A Tie consists of a token ({@link Token}) and a dataExpression
 * ({@link ValueExpression}). First the dataExpression is evaluated. Then each
 * value is used as an input to parse the token in. Tie succeeds if all parses
 * of the token in all results succeed. Tie fails if the dataExpression
 * evaluates to a list of values that is either empty or contains an invalid
 * value.
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
    protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ImmutableList<OptionalValue> dataResult = dataExpression.eval(environment, encoding);
        if (dataResult.isEmpty()) {
            return failure(environment);
        }
        final ParseResult result = iterate(scope, dataResult, 0, environment.addBranch(this), encoding);
        if (result.succeeded) {
            return success(new Environment(result.environment.closeBranch().order, environment.source, environment.offset, environment.callbacks));
        }
        return failure(environment);
    }

    private ParseResult iterate(final String scope, final ImmutableList<OptionalValue> values, final int index, final Environment environment, final Encoding encoding) throws IOException {
        if (!values.head.isPresent()) {
            return failure(environment);
        }
        final ParseResult result = token.parse(scope, environment.source(dataExpression, index, environment, encoding), encoding);
        if (result.succeeded) {
            if (values.tail.isEmpty()) { return result; }
            return iterate(scope, values.tail, index + 1, result.environment, encoding);
        }
        return failure(environment);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + dataExpression + ")";
    }

}
