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

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;

public abstract class Token {

    public static final String SEPARATOR = ".";

    public final String name;
    public final Encoding enc;

    protected Token(final String name, final Encoding enc) {
        this.name = checkNotNull(name, "name");
        this.enc = enc;
    }

    public ParseResult parse(final String scope, final Environment env, final Encoding enc) throws IOException {
        return parseImpl(makeScope(scope), env, this.enc != null ? this.enc : enc);
    }

    public ParseResult parse(final Environment env, final Encoding enc) throws IOException {
        return parse(name, env, enc);
    }

    protected abstract ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException;

    private String makeScope(final String scope) {
        return scope + (scope.isEmpty() || name.isEmpty() ? "" : SEPARATOR) + name;
    }

    protected String makeNameFragment() {
        return name.isEmpty() ? "" : name + ",";
    }

}
