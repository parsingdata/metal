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

import static io.parsingdata.metal.Shorthand.TRUE;
import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.success;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;

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
        this.predicate = predicate == null ? TRUE : predicate;
    }

    @Override
    protected Optional<ParseState> parseImpl(final String scope, final ParseState parseState, final Callbacks callbacks, final Encoding encoding) {
        return iterate(scope, parseState.addBranch(this), callbacks, encoding).computeResult();
    }

    private Trampoline<Optional<ParseState>> iterate(final String scope, final ParseState parseState, final Callbacks callbacks, final Encoding encoding) {
        if (predicate.eval(parseState.order, encoding)) {
            return token
                .parse(scope, parseState, callbacks, encoding)
                .map(nextParseState -> intermediate(() -> iterate(scope, nextParseState, callbacks, encoding)))
                .orElseGet(() -> complete(Util::failure));
        }
        return complete(() -> success(parseState.closeBranch()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + "," + predicate + ")";
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
