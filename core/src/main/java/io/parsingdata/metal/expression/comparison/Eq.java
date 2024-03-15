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

package io.parsingdata.metal.expression.comparison;

import java.util.Arrays;

import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ComparisonExpression} that implements byte-level equality.
 * <p>
 * Both values are interpreted as byte arrays. The
 * {@link #compare(Value, Value)} method returns <code>true</code> if both
 * values represent the same array. That is, if both arrays are exactly the
 * same size and both arrays have the same byte value at every valid index.
 *
 * @see EqNum
 * @see EqStr
 */
public class Eq extends ComparisonExpression {

    public Eq(final ValueExpression value, final ValueExpression predicate) {
        super(value, predicate);
    }

    @Override
    public boolean compare(final Value left, final Value right) {
        return left.slice().length.compareTo(right.slice().length) == 0
            && Arrays.equals(left.value(), right.value());
    }

}
