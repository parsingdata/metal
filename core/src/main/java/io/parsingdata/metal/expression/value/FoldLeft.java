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
import static io.parsingdata.metal.data.Selection.reverse;

import java.util.function.BinaryOperator;

import io.parsingdata.metal.data.ImmutableList;

/**
 * A {@link ValueExpression} implementation of the FoldLeft operation.
 * <p>
 * FoldLeft differs from {@link FoldRight} in that the reduce operation is
 * applied from left to right (i.e., starting at the top).
 *
 * @see FoldRight
 */
public class FoldLeft extends Fold {

    public FoldLeft(final ValueExpression values, final BinaryOperator<ValueExpression> reducer, final ValueExpression initial) {
        super(values, reducer, initial);
    }

    @Override
    protected ImmutableList<Value> prepareValues(final ImmutableList<Value> valueList) {
        return reverse(valueList);
    }

    @Override
    protected ValueExpression reduce(final BinaryOperator<ValueExpression> reducer, final Value head, final Value tail) {
        return reducer.apply(con(head), con(tail));
    }

}
