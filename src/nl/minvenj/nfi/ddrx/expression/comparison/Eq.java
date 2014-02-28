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

package nl.minvenj.nfi.ddrx.expression.comparison;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Val;

public class Eq<T extends Val> extends ComparisonExpression<T> {
    
    public Eq(ValueExpression<T> value, ValueExpression<T> predicate) {
        super(value, predicate);
    }

    @Override
    public boolean eval(Environment env) {
        byte[] v = _value.eval(env).getData();
        byte[] p = _predicate.eval(env).getData();
        if (v.length != p.length) {
            return false;
        }
        for (int i = 0; i < v.length; i++) {
            if (v[i] != p[i]) {
                return false;
            }
        }
        return true;
    }

}
