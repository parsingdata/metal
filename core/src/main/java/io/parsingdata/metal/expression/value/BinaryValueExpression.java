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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;

import static io.parsingdata.metal.Util.checkNotNull;

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

    private final ValueExpression lop;
    private final ValueExpression rop;

    public BinaryValueExpression(final ValueExpression lop, final ValueExpression rop) {
        this.lop = checkNotNull(lop, "lop");
        this.rop = checkNotNull(rop, "rop");
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        return evalLists(lop.eval(env, enc), rop.eval(env, enc), env, enc);
    }

    private OptionalValueList evalLists(final OptionalValueList lvl, final OptionalValueList rvl, final Environment env, final Encoding enc) {
        if (lvl.isEmpty()) { return makeListWithEmpty(rvl.size); }
        if (rvl.isEmpty()) { return makeListWithEmpty(lvl.size); }
        return evalLists(lvl.tail, rvl.tail, env, enc).add(eval(lvl.head, rvl.head, env, enc));
    }

    private OptionalValueList makeListWithEmpty(final long size) {
        if (size <= 0) { return OptionalValueList.EMPTY; }
        return makeListWithEmpty(size - 1).add(OptionalValue.empty());
    }

    private OptionalValue eval(final OptionalValue left, final OptionalValue right, final Environment env, final Encoding enc) {
        if (!left.isPresent() || !right.isPresent()) { return OptionalValue.empty(); }
        return eval(left.get(), right.get(), env, enc);
    }

    public abstract OptionalValue eval(final Value lv, final Value rv, final Environment env, final Encoding enc);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + lop + "," + rop + ")";
    }

}
