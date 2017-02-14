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
import static io.parsingdata.metal.Util.failure;
import static io.parsingdata.metal.Util.success;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link Token} that specifies a choice out of a list of tokens.
 *<p>
 * A Cho consists of an array of <code>tokens</code>. If none of the tokens
 * succeed, the Cho fails. If any token succeeds, the Cho succeeds. Precedence
 * is from left to right.
 */
public class Cho extends Token {

    private final Token[] tokens; // Private because array content is mutable.

    public Cho(final String name, final Encoding encoding, final Token... tokens) {
        super(name, encoding);
        this.tokens = checkContainsNoNulls(tokens, "tokens");
        if (tokens.length < 2) { throw new IllegalArgumentException("At least two Tokens are required."); }
    }

    public Token[] tokens() {
        return tokens.clone();
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final Optional<Environment> result = iterate(scope, environment.addBranch(this), encoding, 0);
        if (result.isPresent()) {
            return success(result.get().closeBranch());
        }
        return failure();
    }

    private Optional<Environment> iterate(final String scope, final Environment environment, final Encoding encoding, final int index) throws IOException {
        if (index >= tokens.length) {
            return failure();
        }
        final Optional<Environment> result = tokens[index].parse(scope, environment, encoding);
        if (result.isPresent()) { return result; }
        return iterate(scope, environment, encoding, index + 1);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + Util.tokensToString(tokens) + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Arrays.equals(tokens, ((Cho)obj).tokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), Arrays.hashCode(tokens));
    }

}
