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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.True;

import java.io.IOException;

import static io.parsingdata.metal.Util.checkNotNull;

public class Str extends Token {

    public final String scope;
    public final Token token;
    public final StructSink sink;
    public final Expression predicate;

    public Str(final String scope, final Token token, final Encoding enc, final StructSink sink, final Expression predicate) {
        super(enc);
        this.scope = checkNotNull(scope, "scope");
        this.token = checkNotNull(token, "token");
        this.sink = sink;
        this.predicate = predicate == null ? new True() : predicate;
    }

    @Override
    protected ParseResult parseImpl(final String outerScope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = token.parse(outerScope + "." + scope, new Environment(env.order.addBranch(this), env.input, env.offset), enc);
        if (!res.succeeded) { return new ParseResult(false, env); }
        final ParseResult closedResult = new ParseResult(true, new Environment(res.environment.order.closeBranch(), res.environment.input, res.environment.offset));
        if (sink != null && predicate.eval(closedResult.environment, enc)) {
            sink.handleStruct(outerScope, closedResult.environment, enc, closedResult.environment.order.get(this).asGraph());
        }
        return closedResult;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + scope + "\"," + token + (sink != null ? "," + sink : "") + ")";
    }

}
