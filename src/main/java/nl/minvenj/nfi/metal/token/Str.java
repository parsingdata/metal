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

    public final String scope;
    private final Token _op;
    private final StructSink _sink;
    private final Expression _pred;

    public Str(final String scope, final Token op, final Encoding enc, final StructSink sink, final Expression pred) {
        super(enc);
        this.scope = checkNotNull(scope, "scope");
        _op = checkNotNull(op, "op");
        _sink = sink;
        _pred = pred == null ? new True() : pred;
    }

    @Override
    protected ParseResult parseImpl(final String outerScope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = _op.parse(outerScope + "." + scope, new Environment(env.order.addBranch(this), env.input, env.offset), enc);
        if (!res.succeeded()) { return new ParseResult(false, env); }
        final ParseResult closedResult = new ParseResult(true, new Environment(res.getEnvironment().order.closeBranch(), res.getEnvironment().input, res.getEnvironment().offset));
        if (_sink != null && _pred.eval(closedResult.getEnvironment(), enc)) {
            _sink.handleStruct(outerScope, closedResult.getEnvironment(), enc, closedResult.getEnvironment().order.get(this).asGraph());
        }
        return closedResult;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + scope + "\"," + _op + (_sink != null ? "," + _sink : "") + ")";
    }

}
