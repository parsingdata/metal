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

package nl.minvenj.nfi.metal.expression.value;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;


public abstract class BinaryValueExpression implements ValueExpression {

    private final ValueExpression _lop;
    private final ValueExpression _rop;

    public BinaryValueExpression(final ValueExpression lop, final ValueExpression rop) {
        if (lop == null) { throw new IllegalArgumentException("Argument lop may not be null."); }
        _lop = lop;
        if (rop == null) { throw new IllegalArgumentException("Argument rop may not be null."); }
        _rop = rop;
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final OptionalValue lv = _lop.eval(env, enc);
        if (!lv.isPresent()) { return lv; }
        final OptionalValue rv = _rop.eval(env, enc);
        if (!rv.isPresent()) { return rv; }
        return eval(lv.get(), rv.get(), env, enc);
    }

    public abstract OptionalValue eval(final Value lv, final Value rv, final Environment env, final Encoding enc);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _lop + "," + _rop + ")";
    }

}
