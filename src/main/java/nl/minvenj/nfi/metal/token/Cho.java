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

package nl.minvenj.nfi.metal.token;

import static nl.minvenj.nfi.metal.Util.checkContainsNoNulls;

import java.io.IOException;
import java.util.Arrays;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.encoding.Encoding;

public class Cho extends Token {

    private final Token[] _tokens;

    public Cho(final Encoding enc, final Token... tokens) {
        super(enc);
        _tokens = checkContainsNoNulls(tokens, "tokens");
        if (tokens.length < 2) { throw new IllegalArgumentException("At least two Tokens are required."); }
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = iterate(scope, new Environment(env.order.addBranch(this), env.input, env.offset), enc, 0);
        if (res.succeeded()) { return new ParseResult(true, new Environment(res.getEnvironment().order.closeBranch(), res.getEnvironment().input, res.getEnvironment().offset)); }
        return new ParseResult(false, env);
    }

    private ParseResult iterate(final String scope, final Environment env, final Encoding enc, final int index) throws IOException {
        if (index >= _tokens.length) { return new ParseResult(false, env); }
        final ParseResult res = _tokens[index].parse(scope, env, enc);
        if (res.succeeded()) { return res; }
        return iterate(scope, env, enc, index + 1);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + Arrays.toString(_tokens) + ")";
    }

}
