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
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;

import java.io.IOException;

import static io.parsingdata.metal.Util.checkNotNull;

public class Nod extends Token {

    private final ValueExpression _size;

    public Nod(final ValueExpression size, final Encoding enc) {
        super(enc);
        _size = checkNotNull(size, "size");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final OptionalValueList ov = _size.eval(env, enc);
        if (ov.isEmpty()) { return new ParseResult(false, env); }
        if (ov.size > 1) { throw new RuntimeException("A single size value must be provided."); }
        return ov.head.isPresent() ? new ParseResult(true, new Environment(env.order, env.input, env.offset + ov.head.get().asNumeric().longValue())) : new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _size + ")";
    }

}
