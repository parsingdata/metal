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

package nl.minvenj.nfi.ddrx.expression.value;

import nl.minvenj.nfi.ddrx.data.Environment;


public abstract class BinaryValueExpression implements ValueExpression {

    private final ValueExpression _lop;
    private final ValueExpression _rop;

    public BinaryValueExpression(ValueExpression lop, ValueExpression rop) {
        _lop = lop;
        _rop = rop;
    }

    @Override
    public OptionalValue eval(Environment env) {
        final OptionalValue lv = _lop.eval(env);
        if (!lv.isPresent()) { return lv; }
        final OptionalValue rv = _rop.eval(env);
        if (!rv.isPresent()) { return rv; }
        return eval(lv.get(), rv.get(), env);
    }

    public abstract OptionalValue eval(Value lv, Value rv, Environment env);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _lop + "," + _rop + ")";
    }

}
