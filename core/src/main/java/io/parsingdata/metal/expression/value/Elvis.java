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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Expression for the 'elvis operator': <pre>?:</pre>.
 * <p>
 * Example:
 *
 * <pre>
 *   elvis(ref("foo"), ref("bar"))
 * </pre>
 *
 * If <code>ref("foo")</code> can be successfully evaluated, this elvis-expression
 * evaluates to that value, else it evaluates to the value of <code>ref("bar")</code>.
 *
 * For lists, values with the same index are compared in this manner. If lists are of
 * unequal length, the shortest list is virtually extended with empty values (i.e.,
 * the values in the other list are returned at those locations).
 */
public class Elvis implements ValueExpression {
    public final ValueExpression left;
    public final ValueExpression right;

    public Elvis(final ValueExpression left, final ValueExpression right) {
        this.left = checkNotNull(left, "left");
        this.right = checkNotNull(right, "right");
    }

    @Override
    public OptionalValueList eval(final Environment environment, final Encoding encoding) {
        return eval(left.eval(environment, encoding), right.eval(environment, encoding));
    }

    private OptionalValueList eval(final OptionalValueList leftValues, final OptionalValueList rightValues) {
        if (leftValues.isEmpty()) { return rightValues; }
        if (rightValues.isEmpty()) { return leftValues; }
        return eval(leftValues.tail, rightValues.tail).add(leftValues.head.isPresent() ? leftValues.head : rightValues.head);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + left + "," + right + ")";
    }
}
