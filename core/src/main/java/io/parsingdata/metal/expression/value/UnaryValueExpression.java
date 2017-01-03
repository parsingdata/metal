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
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Base class for {@link ValueExpression}s with one operand.
 * <p>
 * A UnaryValueExpression implements a ValueExpression that has one
 * <code>operand</code> (a {@link ValueExpression}). The operand is first
 * evaluated. If it evaluates to {@link OptionalValue#empty()}, the result of
 * the ValueExpression itself will we that as well.
 * <p>
 * To implement a UnaryValueExpression, only the
 * {@link #eval(Value, Environment, Encoding)} must be implemented, handling
 * the case of evaluating one value. This base class takes care of evaluating
 * the operand and handling list semantics.
 */
public abstract class UnaryValueExpression implements ValueExpression {

    public final ValueExpression operand;

    public UnaryValueExpression(final ValueExpression operand) {
        this.operand = checkNotNull(operand, "operand");
    }

    @Override
    public ImmutableList<OptionalValue> eval(final Environment environment, final Encoding encoding) {
        return eval(operand.eval(environment, encoding), environment, encoding);
    }

    private ImmutableList<OptionalValue> eval(final ImmutableList<OptionalValue> values, final Environment environment, final Encoding encoding) {
        if (values.isEmpty()) { return values; }
        return eval(values.tail, environment, encoding).add(values.head.isPresent() ? eval(values.head.get(), environment, encoding) : values.head);
    }

    public abstract OptionalValue eval(final Value value, final Environment environment, final Encoding encoding);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operand + ")";
    }

}
