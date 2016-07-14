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
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.True;
import io.parsingdata.metal.expression.value.ValueExpression;

import java.io.IOException;

import static io.parsingdata.metal.Util.checkNotNull;

public class Def extends Token {

    private final String _name;
    private final ValueExpression _size;
    private final Expression _pred;

    public Def(final String name, final ValueExpression size, final Expression pred, final Encoding enc) {
        super(enc);
        _name = checkNotNull(name, "name");
        _size = checkNotNull(size, "size");
        _pred = pred == null ? new True() : pred;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final OptionalValueList size = _size.eval(env, enc);
        if (size.size != 1) { throw new IllegalStateException("Size may not evaluate to more than a single value."); }
        if (!size.head.isPresent()) {
            return new ParseResult(false, env);
        }
        // TODO: Handle value expression results as BigInteger (#16)
        final int dataSize = size.head.get().asNumeric().intValue();
        if (dataSize < 0) {
            return new ParseResult(false, env);
        }
        final byte[] data = new byte[dataSize];
        if (env.input.read(env.offset, data) != data.length) {
            return new ParseResult(false, env);
        }
        final Environment newEnv = new Environment(env.order.add(new ParseValue(scope, _name, this, env.offset, data, enc)), env.input, env.offset + dataSize);
        return _pred.eval(newEnv, enc) ? new ParseResult(true, newEnv) : new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _name + "\"," + _size + "," + _pred + ",)";
    }

}
