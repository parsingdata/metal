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

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Base class for ValueExpressions with two operands.
 *
 * Subclasses implement behaviour for evaluating two operands and returning a single
 * value as a result. If one operand evaluates to empty, the result is always empty.
 *
 * For lists, values with the same index are evaluated in this manner. If lists are of
 * unequal length, the result is a list with evaluated values the same size as the
 * shortest list, appended with empty values to match the size of the longest list.
 */
public abstract class BinaryValueExpression implements ValueExpression {

    public final ValueExpression left;
    public final ValueExpression right;

    public BinaryValueExpression(final ValueExpression left, final ValueExpression right) {
        this.left = checkNotNull(left, "left");
        this.right = checkNotNull(right, "right");
    }

    @Override
    public ImmutableList<OptionalValue> eval(final Environment environment, final Encoding encoding) throws IOException {
        return evalLists(left.eval(environment, encoding), right.eval(environment, encoding), environment, encoding);
    }

    private ImmutableList<OptionalValue> evalLists(final ImmutableList<OptionalValue> leftValues, final ImmutableList<OptionalValue> rightValues, final Environment environment, final Encoding encoding) throws IOException {
        if (leftValues.isEmpty()) {
            return makeListWithEmpty(rightValues.size);
        }
        if (rightValues.isEmpty()) {
            return makeListWithEmpty(leftValues.size);
        }
        return evalLists(leftValues.tail, rightValues.tail, environment, encoding).add(eval(leftValues.head, rightValues.head, environment, encoding));
    }

    private ImmutableList<OptionalValue> makeListWithEmpty(final long size) {
        if (size <= 0) { return new ImmutableList<>(); }
        return makeListWithEmpty(size - 1).add(OptionalValue.empty());
    }

    private OptionalValue eval(final OptionalValue left, final OptionalValue right, final Environment environment, final Encoding encoding) throws IOException {
        if (!left.isPresent() || !right.isPresent()) {
            return OptionalValue.empty();
        }
        return eval(left.get(), right.get(), environment, encoding);
    }

    public abstract OptionalValue eval(final Value left, final Value right, final Environment environment, final Encoding encoding) throws IOException;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + left + "," + right + ")";
    }

}
