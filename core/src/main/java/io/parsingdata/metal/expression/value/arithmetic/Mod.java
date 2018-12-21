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

package io.parsingdata.metal.expression.value.arithmetic;

import static java.math.BigInteger.ZERO;

import java.util.Optional;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link BinaryValueExpression} that implements the modulo operation.
 * <p>
 * If one of the values resulting from evaluating <code>divisors</code> is equal
 * to or small than zero, the associated result will be empty.
 */
public class Mod extends BinaryValueExpression {

    public Mod(final ValueExpression dividends, final ValueExpression divisors) {
        super(dividends, divisors);
    }

    @Override
    public Optional<Value> eval(final Value dividend, final Value divisor, final ParseState parseState, final Encoding encoding) {
        if (divisor.asNumeric().compareTo(ZERO) <= 0) {
            return Optional.empty();
        }
        return Optional.of(ConstantFactory.createFromNumeric(dividend.asNumeric().mod(divisor.asNumeric()), encoding));
    }

}
