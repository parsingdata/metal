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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;

public abstract class UnaryValueExpression implements ValueExpression {

    protected final ValueExpression _op;

    public UnaryValueExpression(final ValueExpression op) {
        _op = checkNotNull(op, "op");
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final OptionalValue v = _op.eval(env, enc);
        if (!v.isPresent()) { return v; }
        return eval(v.get(), env, enc);
    }

    public abstract OptionalValue eval(final Value v, final Environment env, final Encoding enc);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _op + ")";
    }

}
