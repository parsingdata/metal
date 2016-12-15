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

import static io.parsingdata.metal.Util.checkContainsNoNulls;
import static io.parsingdata.metal.data.ParseResult.failure;
import static io.parsingdata.metal.data.ParseResult.success;

import java.io.IOException;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link Token} that specifies a dependency between a list of tokens.
 *
 * A Seq consists of an array of tokens. If one of the tokens doesn't
 * succeed, the Seq fails. All of the tokens succeeding will result in the Seq
 * succeeding. Order is from left to right.
 */
public class Seq extends Token {

    private final Token[] tokens; // Private because array content is mutable.

    public Seq(final String name, final Encoding encoding, final Token... tokens) {
        super(name, encoding);
        this.tokens = checkContainsNoNulls(tokens, "tokens");
        if (tokens.length < 2) { throw new IllegalArgumentException("At least two Tokens are required."); }
    }

    public Token[] tokens() {
        return tokens.clone();
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ParseResult result = iterate(scope, environment.addBranch(this), encoding, 0);
        if (result.succeeded) {
            return success(result.environment.closeBranch());
        }
        return failure(environment);
    }

    private ParseResult iterate(final String scope, final Environment environment, final Encoding encoding, final int index) throws IOException {
        if (index >= tokens.length) {
            return success(environment);
        }
        final ParseResult result = tokens[index].parse(scope, environment, encoding);
        if (result.succeeded) {
            return iterate(scope, result.environment, encoding, index + 1);
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + Util.tokensToString(tokens) + ")";
    }

}
