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

package io.parsingdata.metal.expression.value.arithmetic;

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

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
 * If the value of the <code>right</code> operand is equal to or smaller than
 * zero, the result is empty.
 */
public class Mod extends BinaryValueExpression {

    public Mod(final ValueExpression left, final ValueExpression right) {
        super(left, right);
    }

    @Override
    public Optional<Value> eval(final Value leftValue, final Value rightValue, final ParseState parseState, final Encoding encoding) {
        if (rightValue.asNumeric().compareTo(ZERO) <= 0) {
            return Optional.of(NOT_A_VALUE);
        }
        return Optional.of(ConstantFactory.createFromNumeric(leftValue.asNumeric().mod(rightValue.asNumeric()), encoding));
    }

}
