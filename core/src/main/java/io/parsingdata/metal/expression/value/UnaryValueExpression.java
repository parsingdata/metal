/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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
 * Base class for {@link ValueExpression}s with one operand.
 * <p>
 * A UnaryValueExpression implements a ValueExpression that has one
 * <code>operand</code> (a {@link ValueExpression}). The operand is first
 * evaluated. If it evaluates to {@link Optional#empty()}, the result of the
 * ValueExpression itself will be that as well.
 * <p>
 * To implement a UnaryValueExpression, only the
 * {@link #eval(Value, ParseState, Encoding)} must be implemented, handling
 * the case of evaluating one value. This base class takes care of evaluating
 * the operand and handling list semantics.
 *
 * @see BinaryValueExpression
 */
public abstract class UnaryValueExpression extends ImmutableObject implements ValueExpression {

    public final ValueExpression operand;

    public UnaryValueExpression(final ValueExpression operand) {
        this.operand = checkNotNull(operand, "operand");
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        return reverse(eval(operand.eval(parseState, encoding), parseState, encoding, new ImmutableList<>()).computeResult());
    }

    private Trampoline<ImmutableList<Value>> eval(final ImmutableList<Value> values, final ParseState parseState, final Encoding encoding, final ImmutableList<Value> result) {
        if (values.isEmpty()) {
            return complete(() -> result);
        }
        return intermediate(() -> eval(values.tail(), parseState, encoding, result.addHead(safeEval(values.head(), parseState, encoding))));
    }

    public abstract Optional<Value> eval(final Value value, final ParseState parseState, final Encoding encoding);

    private Value safeEval(final Value value, final ParseState parseState, final Encoding encoding) {
        if (value.equals(NOT_A_VALUE)) {
            return NOT_A_VALUE;
        }
        return eval(value, parseState, encoding)
            .orElse(NOT_A_VALUE);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operand + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(operand, ((UnaryValueExpression)obj).operand);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), operand);
    }

}
