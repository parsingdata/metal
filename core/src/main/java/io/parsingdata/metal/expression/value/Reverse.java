/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static io.parsingdata.metal.data.Selection.reverse;

import java.util.Objects;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that reverses the results of its operand.
 * <p>
 * Reverse has a single operand <code>values</code> (a
 * {@link ValueExpression}). When evaluated, it evaluates <code>values</code>
 * and then reverses and returns the result.
 */
public class Reverse extends ImmutableObject implements ValueExpression {

    public final ValueExpression values;

    public Reverse(final ValueExpression values) {
        this.values = values;
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        return reverse(values.eval(parseState, encoding));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + values + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(values, ((Reverse)obj).values);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), values);
    }

}
