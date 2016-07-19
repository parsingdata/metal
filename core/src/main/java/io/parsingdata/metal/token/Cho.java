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

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

import java.io.IOException;

import static io.parsingdata.metal.Util.checkContainsNoNulls;

public class Cho extends Token {

    private final Token[] tokens;

    public Cho(final Encoding enc, final Token... tokens) {
        super(enc);
        this.tokens = checkContainsNoNulls(tokens, "tokens");
        if (tokens.length < 2) { throw new IllegalArgumentException("At least two Tokens are required."); }
    }

    public Token[] tokens() {
        return tokens.clone();
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = iterate(scope, new Environment(env.order.addBranch(this), env.input, env.offset), enc, 0);
        if (res.succeeded) { return new ParseResult(true, new Environment(res.environment.order.closeBranch(), res.environment.input, res.environment.offset)); }
        return new ParseResult(false, env);
    }

    private ParseResult iterate(final String scope, final Environment env, final Encoding enc, final int index) throws IOException {
        if (index >= tokens.length) { return new ParseResult(false, env); }
        final ParseResult res = tokens[index].parse(scope, env, enc);
        if (res.succeeded) { return res; }
        return iterate(scope, env, enc, index + 1);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + Util.tokensToString(tokens) + ")";
    }

}
