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

import java.io.IOException;

import static io.parsingdata.metal.Util.checkNotNull;

public class Rep extends Token {

    public final Token token;

    public Rep(final String name, final Token token, final Encoding enc) {
        super(name, enc);
        this.token = checkNotNull(token, "token");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = iterate(scope, new Environment(env.order.addBranch(this), env.input, env.offset), enc);
        return new ParseResult(true, new Environment(res.environment.order.closeBranch(), res.environment.input, res.environment.offset));
    }

    private ParseResult iterate(final String scope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = token.parse(scope, env, enc);
        if (res.succeeded) { return iterate(scope, res.environment, enc); }
        return new ParseResult(true, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (name.length() > 0 ? name + "," : "") + token + ")";
    }

}
