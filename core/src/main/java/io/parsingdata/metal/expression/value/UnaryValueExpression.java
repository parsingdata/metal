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
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Base class for {@link ValueExpression}s with one operand.
 * <p>
 * A UnaryValueExpression implements a ValueExpression that has a single <code>operands</code>
 * field (a {@link ValueExpression}). <code>operands</code> is first evaluated. If it evaluates
 * to {@link Optional#empty()}, the result of the ValueExpression itself will be that as well.
 * <p>
 * To implement a UnaryValueExpression, only the {@link #eval(Value, ParseState, Encoding)}
 * must be implemented, handling the case of evaluating one value. This base class takes care
 * of evaluating the field and handling list semantics.
 *
 * @see BinaryValueExpression
 */
public abstract class UnaryValueExpression implements ValueExpression {

    public final ValueExpression operands;

    public UnaryValueExpression(final ValueExpression operands) {
        this.operands = checkNotNull(operands, "operands");
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        return reverse(eval(operands.eval(parseState, encoding), parseState, encoding, new ImmutableList<>()).computeResult());
    }

    private Trampoline<ImmutableList<Optional<Value>>> eval(final ImmutableList<Optional<Value>> evaluatedValues, final ParseState parseState, final Encoding encoding, final ImmutableList<Optional<Value>> result) {
        if (evaluatedValues.isEmpty()) {
            return complete(() -> result);
        }
        return intermediate(() -> eval(evaluatedValues.tail, parseState, encoding, result.add(evaluatedValues.head.flatMap(value -> eval(value, parseState, encoding)))));
    }

    public abstract Optional<Value> eval(final Value value, final ParseState parseState, final Encoding encoding);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operands + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(operands, ((UnaryValueExpression)obj).operands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), operands);
    }

}
