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
import nl.minvenj.nfi.ddrx.data.ParseResult;
import nl.minvenj.nfi.ddrx.data.ParsedValueList;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.value.ParsedValue;

public class Sub extends Token {

    private final Token _op;
    private final String _refName;

    public Sub(final Token op, final String refName, final Encoding enc) {
        super(enc);
        if (op == null) { throw new IllegalArgumentException("Argument op may not be null."); }
        _op = op;
        _refName = refName;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult res = _op.parse(scope, env, enc);
        if (res.succeeded()) {
            final ParsedValueList sub = res.getEnvironment().order.getValuesSincePrefix(env.order.head);
            final ParsedValue ref = _refName == null ? sub.head : sub.get(_refName);
            if (ref != null && !res.getEnvironment().order.containsOffset(ref.asNumeric().longValue())) {
                return new ParseResult(true, new Environment(res.getEnvironment().order, res.getEnvironment().input, ref.asNumeric().longValue()));
            }
        }
        return res;
    }

}
