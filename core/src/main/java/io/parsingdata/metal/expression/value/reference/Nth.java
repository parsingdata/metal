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

package io.parsingdata.metal.expression.value.reference;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.OptionalValueList.EMPTY;

import java.math.BigInteger;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * Expression for the 'nth operator':
 * <p>
 * Example:
 *
 * <pre>
 *   nth(ref("value"), ref("index"))
 * </pre>
 *
 * Nth takes as arguments two lists, a list of values and a list of indices. The result
 * of the expression is a new list, where for each index <tt>i</tt> in the list of indices,
 * the value at index <tt>i</tt> is taken from the list of values.
 * <p>
 * Should an index be invalid, an empty value is added instead. The resulting list is
 * therefore always of the same size as the list of indices.
 */
public class Nth implements ValueExpression {

    public final ValueExpression values;
    public final ValueExpression indices;

    public Nth(final ValueExpression values, final ValueExpression indices) {
        this.values = checkNotNull(values, "values");
        this.indices = checkNotNull(indices, "indices");
    }

    @Override
    public OptionalValueList eval(final Environment environment, final Encoding encoding) {
        return eval(values.eval(environment, encoding), indices.eval(environment, encoding));
    }

    private OptionalValueList eval(final OptionalValueList values, final OptionalValueList indices) {
        if (indices.isEmpty()) { return EMPTY; }
        if (indices.head.isPresent()) {
            final BigInteger index = indices.head.get().asNumeric();
            final BigInteger valueCount = BigInteger.valueOf(values.size);
            if (index.compareTo(valueCount) < 0 && index.compareTo(ZERO) >= 0) {
                return eval(values, indices.tail).add(nth(values, valueCount.subtract(index).subtract(ONE)));
            }
        }
        return eval(values, indices.tail).add(OptionalValue.empty());
    }

    private OptionalValue nth(final OptionalValueList values, final BigInteger index) {
        if (index.equals(ZERO)) {
            return values.head;
        }
        return nth(values.tail, index.subtract(ONE));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + values + "," + indices + ")";
    }

}
