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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

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
    protected Optional<ParseState> parseImpl(final String scope, final ParseState parseState, final Callbacks callbacks, final Encoding encoding) {
        final ImmutableList<Optional<Value>> dataResult = dataExpression.eval(parseState.order, encoding);
        if (dataResult.isEmpty()) {
            return failure();
        }
        return iterate(scope, dataResult, 0, parseState, parseState.addBranch(this), callbacks, encoding).computeResult();
    }

    private Trampoline<Optional<ParseState>> iterate(final String scope, final ImmutableList<Optional<Value>> values, final int index, final ParseState returnParseState, final ParseState parseState, final Callbacks callbacks, final Encoding encoding) {
        if (values.isEmpty()) {
            return complete(() -> success(new ParseState(parseState.closeBranch().order, returnParseState.source, returnParseState.offset)));
        }
        if (!values.head.isPresent()) {
            return complete(Util::failure);
        }
        return token
            .parse(scope, parseState.source(dataExpression, index, parseState, encoding), callbacks, encoding)
            .map(nextParseState -> intermediate(() -> iterate(scope, values.tail, index + 1, returnParseState, nextParseState, callbacks, encoding)))
            .orElseGet(() -> complete(Util::failure));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + "," + dataExpression + ")";
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
