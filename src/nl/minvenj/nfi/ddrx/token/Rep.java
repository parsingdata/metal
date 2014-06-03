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

public class Rep extends Token {

    private final Token _op;

    public Rep(Token op, Encoding enc) {
        super(enc);
        _op = op;
    }

    public Rep(Token op) {
        this(op, null);
    }

    @Override
    protected boolean parseImpl(String scope, Environment env, Encoding enc) throws IOException {
        env.mark();
        if (!_op.parse(scope, env, enc)) {
            env.reset();
            return true;
        }
        env.clear();
        return parse(scope, env, enc);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _op + ")";
    }

}
