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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Selection.reverse;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
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
public class Elvis implements ValueExpression {

    public final ValueExpression left;
    public final ValueExpression right;

    public Elvis(final ValueExpression left, final ValueExpression right) {
        this.left = checkNotNull(left, "left");
        this.right = checkNotNull(right, "right");
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseGraph graph, final Encoding encoding) {
        return reverse(eval(new ImmutableList<>(), left.eval(graph, encoding), right.eval(graph, encoding)).computeResult());
    }

    private Trampoline<ImmutableList<Optional<Value>>> eval(final ImmutableList<Optional<Value>> result, final ImmutableList<Optional<Value>> leftValues, final ImmutableList<Optional<Value>> rightValues) {
        if (leftValues.isEmpty()) {
            return complete(() -> result.add(reverse(rightValues)));
        }
        if (rightValues.isEmpty()) {
            return complete(() -> result.add(reverse(leftValues)));
        }
        return intermediate(() -> eval(result.add(leftValues.head.isPresent() ? leftValues.head : rightValues.head), leftValues.tail, rightValues.tail));
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
    public int hashCode() {
        return Objects.hash(getClass().hashCode(), left, right);
    }

}
