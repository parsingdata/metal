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

import static io.parsingdata.metal.Util.checkNotNull;

import java.math.BigInteger;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Nth implements ValueExpression {

    private final ValueExpression values;
    private final ValueExpression indices;

    public Nth(final ValueExpression values, final ValueExpression indices) {
        this.values = checkNotNull(values, "values");
        this.indices = checkNotNull(indices, "indices");
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        return eval(values.eval(env, enc), indices.eval(env, enc));
    }

    private OptionalValueList eval(final OptionalValueList values, final OptionalValueList indices) {
        if (indices.isEmpty()) {
            return OptionalValueList.EMPTY;
        }
        if (indices.head.isPresent()) {
            final BigInteger index = indices.head.get().asNumeric();
            final BigInteger valueCount = BigInteger.valueOf(values.size);
            if (index.compareTo(valueCount) < 0 && index.compareTo(BigInteger.ZERO) >= 0) {
                return eval(values, indices.tail).add(nth(values, valueCount.subtract(index).subtract(BigInteger.ONE)));
            }
        }
        return eval(values, indices.tail).add(OptionalValue.empty());
    }

    private OptionalValue nth(final OptionalValueList values, final BigInteger index) {
        if (index.equals(BigInteger.ZERO)) {
            return values.head;
        }
        return nth(values.tail, index.subtract(BigInteger.ONE));
    }

}
