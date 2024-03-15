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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.data.Selection.reverse;

import java.util.function.BinaryOperator;

import io.parsingdata.metal.data.ImmutableList;

/**
 * A {@link SingleValueExpression} implementation of the FoldLeft operation.
 * <p>
 * FoldLeft differs from {@link FoldRight} in that the reduce operation is
 * applied from left to right (i.e., starting at the top).
 *
 * @see FoldRight
 */
public class FoldLeft extends Fold {

    public FoldLeft(final ValueExpression values, final BinaryOperator<SingleValueExpression> reducer, final SingleValueExpression initial) {
        super(values, reducer, initial);
    }

    @Override
    protected ImmutableList<Value> prepareValues(final ImmutableList<Value> valueList) {
        return reverse(valueList);
    }

    @Override
    protected SingleValueExpression reduce(final BinaryOperator<SingleValueExpression> reducer, final Value head, final Value tail) {
        return reducer.apply(con(head), con(tail));
    }

}
