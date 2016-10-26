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
import static io.parsingdata.metal.data.ParseResult.failure;
import static io.parsingdata.metal.data.ParseResult.success;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;

public class While extends Token {

    public final Token token;
    public final Expression predicate;

    public While(final String name, final Token token, final Expression predicate, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.predicate = predicate == null ? expTrue() : predicate;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ParseResult result = iterate(scope, environment.addBranch(this), encoding);
        if (result.succeeded) {
            return success(result.environment.closeBranch());
        }
        return failure(environment);
    }

    private ParseResult iterate(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        if (!predicate.eval(environment, encoding)) {
            return success(environment);
        }
        final ParseResult result = token.parse(scope, environment, encoding);
        if (result.succeeded) {
            return iterate(scope, result.environment, encoding);
        }
        return failure(environment);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + predicate + ")";
    }

}
