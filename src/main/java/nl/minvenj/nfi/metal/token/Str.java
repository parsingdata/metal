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
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.Expression;
import nl.minvenj.nfi.metal.expression.True;

public class Str extends Token {

    private final String _scope;
    private final Token _op;
    private final StructSink _sink;
    private final Expression _pred;

    public Str(final String scope, final Token op, final Encoding enc, final StructSink sink, final Expression pred) {
        super(enc);
        _scope = checkNotNull(scope, "scope");
        _op = checkNotNull(op, "op");
        _sink = sink;
        _pred = pred == null ? new True() : pred;
    }

    @Override
    protected ParseResult parseImpl(final String outerScope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = _op.parse(outerScope + "." + _scope, env, enc);
        if (res.succeeded() && _sink != null && _pred.eval(res.getEnvironment(), enc)) {
            _sink.handleStruct(outerScope, res.getEnvironment(), enc, null /*res.getEnvironment().order.getGraphAt(env.order.current())*/);
        }
        return res;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _scope + "\"," + _op + (_sink != null ? "," + _sink : "") + ")";
    }

}
