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

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

public class TokenRef extends Token {

    public final String refName;

    public TokenRef(String name, String refName, Encoding enc) {
        super(name, enc);
        this.refName = checkNotNull(refName, "refName");
        if (refName.isEmpty()) { throw new IllegalArgumentException("Argument refName may not be empty."); }
    }

    @Override
    protected ParseResult parseImpl(String scope, Environment env, Encoding enc) throws IOException {
        final Token referenced = findToken(env.order, refName);
        if (referenced == null) { return failure(env); }
        return referenced.parse(scope, env, enc);
    }

    private Token findToken(final ParseItem item, final String refName) {
        if (item.getDefinition().name.equals(refName)) { return item.getDefinition(); }
        if (item.isGraph() && !item.asGraph().isEmpty()) {
            final Token headResult = findToken(item.asGraph().head, refName);
            if (headResult != null) { return headResult; }
            return findToken(item.asGraph().tail, refName);
        }
        return null;
    }

    @Override
    public Token getDefinition(Environment env) {
        return findToken(env.order, refName);
    }
}
