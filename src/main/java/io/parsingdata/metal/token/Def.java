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

import static nl.minvenj.nfi.metal.Util.checkNotNull;

import java.io.IOException;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.data.ParseValue;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.Expression;
import nl.minvenj.nfi.metal.expression.True;
import nl.minvenj.nfi.metal.expression.value.OptionalValue;
import nl.minvenj.nfi.metal.expression.value.ValueExpression;

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
        final OptionalValue size = _size.eval(env, enc);
        if (!size.isPresent()) {
            return new ParseResult(false, env);
        }
        // TODO: Handle value expression results as BigInteger (METAL-15)
        final int dataSize = size.get().asNumeric().intValue();
        if (dataSize < 0) {
            return new ParseResult(false, env);
        }
        final byte[] data = new byte[dataSize];
        if (env.input.read(env.offset, data) != data.length) {
            return new ParseResult(false, env);
        }
        final Environment newEnv = new Environment(env.order.add(new ParseValue(scope, _name, this, env.offset, data, enc)), env.input, env.offset + size.get().asNumeric().intValue());
        return _pred.eval(newEnv, enc) ? new ParseResult(true, newEnv) : new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _name + "\"," + _size + "," + _pred + ",)";
    }

}
