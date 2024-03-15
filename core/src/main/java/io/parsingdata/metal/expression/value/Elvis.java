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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Selection.reverse;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that implements the Elvis operator:
 * <pre>?:</pre>.
 * <p>
 * An Elvis expression has two operands: <code>left</code> and
 * <code>right</code> (both {@link ValueExpression}s). Both operands are
 * evaluated. The return value is a list with the size of the longest list
 * returned by the two evaluations. At each index, the value at that index in
 * the result returned by evaluating <code>left</code> is placed, except if it
 * does not exist or is {@link Optional#empty()}, in which case the value at
 * that index in the result returned by evaluating right is placed there.
 */
public class Elvis extends ImmutableObject implements ValueExpression {

    public final ValueExpression left;
    public final ValueExpression right;

    public Elvis(final ValueExpression left, final ValueExpression right) {
        this.left = checkNotNull(left, "left");
        this.right = checkNotNull(right, "right");
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        return reverse(eval(new ImmutableList<>(), left.eval(parseState, encoding), right.eval(parseState, encoding)).computeResult());
    }

    private Trampoline<ImmutableList<Value>> eval(final ImmutableList<Value> result, final ImmutableList<Value> leftValues, final ImmutableList<Value> rightValues) {
        if (leftValues.isEmpty()) {
            return complete(() -> result.addList(reverse(rightValues)));
        }
        if (rightValues.isEmpty()) {
            return complete(() -> result.addList(reverse(leftValues)));
        }
        return intermediate(() -> eval(result.addHead(leftValues.head().equals(NOT_A_VALUE) ? rightValues.head() : leftValues.head()), leftValues.tail(), rightValues.tail()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + left + "," + right + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(left, ((Elvis)obj).left)
            && Objects.equals(right, ((Elvis)obj).right);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), left, right);
    }

}
