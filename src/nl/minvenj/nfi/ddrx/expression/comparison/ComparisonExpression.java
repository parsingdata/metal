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

import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Val;

public abstract class ComparisonExpression<T extends Val> implements Expression {
    
    protected final ValueExpression<T> _value;
    protected final ValueExpression<T> _predicate;
    
    public ComparisonExpression(ValueExpression<T> value, ValueExpression<T> predicate) {
        _value = value;
        _predicate = predicate;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _value + "," + _predicate + ")";
    }
    
}
