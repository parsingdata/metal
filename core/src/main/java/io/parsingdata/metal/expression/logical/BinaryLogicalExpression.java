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

package io.parsingdata.metal.expression.logical;

import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Objects;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.expression.Expression;

/**
 * Base class for {@link LogicalExpression} implementations with two operands.
 * <p>
 * A BinaryLogicalExpression has two operands, <code>left</code> and
 * <code>right</code> (both {@link Expression}s). Both operands are evaluated,
 * their results combined using the operator the concrete expression
 * implements and then returned.
 */
public abstract class BinaryLogicalExpression extends ImmutableObject implements LogicalExpression {

    public final Expression left;
    public final Expression right;

    protected BinaryLogicalExpression(final Expression left, final Expression right) {
        this.left = checkNotNull(left, "left");
        this.right = checkNotNull(right, "right");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + left + "," + right + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(left, ((BinaryLogicalExpression)obj).left)
            && Objects.equals(right, ((BinaryLogicalExpression)obj).right);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), left, right);
    }

}
