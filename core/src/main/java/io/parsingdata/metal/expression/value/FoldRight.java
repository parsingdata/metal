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

package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Shorthand.con;

import java.util.Optional;
import java.util.function.BinaryOperator;

import io.parsingdata.metal.data.ImmutableList;

/**
 * A {@link ValueExpression} implementation of the FoldRight operation.
 * <p>
 * FoldRight differs from {@link FoldLeft} in that the reduce operation is
 * applied from right to left (i.e., starting at the bottom).
 *
 * @see FoldLeft
 */
public class FoldRight extends Fold {

    public FoldRight(final ValueExpression values, final BinaryOperator<ValueExpression> reducer, final ValueExpression initial) {
        super(values, reducer, initial);
    }

    @Override
    protected ImmutableList<Optional<Value>> prepareValues(final ImmutableList<Optional<Value>> values) {
        return values;
    }

    @Override
    protected ValueExpression reduce(final BinaryOperator<ValueExpression> reducer, final Value head, final Value tail) {
        return reducer.apply(con(tail), con(head));
    }

}
