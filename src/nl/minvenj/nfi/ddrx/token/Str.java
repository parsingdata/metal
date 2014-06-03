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

package nl.minvenj.nfi.ddrx.token;

import java.io.IOException;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class Str extends Token {

    private final String _scope;
    private final Token _op;
    private final StructSink _sink;

    public Str(String scope, Token op, Encoding enc, StructSink sink) {
        super(enc);
        _scope = scope;
        _op = op;
        _sink = sink;
    }

    public Str(String scope, Token op, Encoding enc) {
        this(scope, op, enc, null);
    }

    public Str(String scope, Token op, StructSink sink) {
        this(scope, op, null, sink);
    }

    public Str(String scope, Token op) {
        this(scope, op, null, null);
    }

    @Override
    protected boolean parseImpl(String outerScope, Environment env, Encoding enc) throws IOException {
        env.pushScope();
        boolean ret = _op.parse(outerScope + "." + _scope, env, enc);
        if (ret && _sink != null) {
            _sink.handleStruct(env.getPrefixInScope(outerScope + "." + _scope));
        }
        env.popScope();
        return ret;
    }

}
