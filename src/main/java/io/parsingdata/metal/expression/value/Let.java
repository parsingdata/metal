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

package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.True;
import io.parsingdata.metal.token.Token;

public class Let extends Token {

    private final String _name;
    private final ValueExpression _value;
    private final Expression _pred;

    public Let(final String name, final ValueExpression value, final Expression pred, final Encoding enc) {
        super(enc);
        _name = checkNotNull(name, "name");
        _value = checkNotNull(value, "value");
        _pred = pred == null ? new True() : pred;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final OptionalValue value = _value.eval(env, enc);
        if (!value.isPresent()) {
            return new ParseResult(false, env);
        }

        final Value val = value.get();
        final Environment newEnv = new Environment(env.order.add(new ParseValue(scope, _name, this, env.offset, val.getValue(), val.getEncoding())), env.input, env.offset);
        return _pred.eval(newEnv, enc) ? new ParseResult(true, newEnv) : new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _name + "\"," + _value + "," + _pred + ",)";
    }
}