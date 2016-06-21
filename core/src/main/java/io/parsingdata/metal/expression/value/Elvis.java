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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Expression for the 'elvis operator': <pre>?:</pre>.
 * <p>
 * Example:
 *
 * <pre>
 *   elvis(ref("foo"), ref("bar"))
 * </pre>
 *
 * If <code>ref("foo")</code> can be successfully evaluated, this elvis-expression
 * evaluates to that value, else it evaluates to the value of <code>ref("bar")</code>.
 */
public class Elvis implements ValueExpression {
    private final ValueExpression _lop;
    private final ValueExpression _rop;

    public Elvis(final ValueExpression lop, final ValueExpression rop) {
        _lop = lop;
        _rop = rop;
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final OptionalValue eval = _lop.eval(env, enc);
        if (eval.isPresent()) {
            return eval;
        }
        return _rop.eval(env, enc);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _lop + "," + _rop + ")";
    }
}
