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

import java.math.BigInteger;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ValueExpression} that returns an indexed list of
 * {@link OptionalValue}s.
 * <p>
 * The Nth ValueExpression has two operands, <code>values</code> and
 * <code>indices</code> (both {@link ValueExpression}s). Both operands are
 * evaluated. Next, the resulting values of evaluating <code>indices</code> is
 * used as a list of integer indices into the results of evaluating
 * <code>values</code>. For every invalid index (such as
 * {@link OptionalValue#empty()}, a negative value or an index that is out of
 * bounds) empty is returned.
 *
 * @see NameRef
 * @see TokenRef
 */
public class Nth implements ValueExpression {

    public final ValueExpression values;
    public final ValueExpression indices;

    public Nth(final ValueExpression values, final ValueExpression indices) {
        this.values = checkNotNull(values, "values");
        this.indices = checkNotNull(indices, "indices");
    }

    @Override
    public ImmutableList<OptionalValue> eval(final Environment environment, final Encoding encoding) {
        return eval(values.eval(environment, encoding), indices.eval(environment, encoding));
    }

    private ImmutableList<OptionalValue> eval(final ImmutableList<OptionalValue> values, final ImmutableList<OptionalValue> indices) {
        if (indices.isEmpty()) { return new ImmutableList<>(); }
        if (indices.head.isPresent()) {
            final BigInteger index = indices.head.get().asNumeric();
            final BigInteger valueCount = BigInteger.valueOf(values.size);
            if (index.compareTo(valueCount) < 0 && index.compareTo(ZERO) >= 0) {
                return eval(values, indices.tail).add(nth(values, valueCount.subtract(index).subtract(ONE)));
            }
        }
        return eval(values, indices.tail).add(OptionalValue.empty());
    }

    private OptionalValue nth(final ImmutableList<OptionalValue> values, final BigInteger index) {
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
