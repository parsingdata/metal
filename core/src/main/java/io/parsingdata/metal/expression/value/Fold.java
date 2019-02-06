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
import static io.parsingdata.metal.expression.value.Value.NOT_A_VALUE;

import java.util.Objects;
import java.util.function.BinaryOperator;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Base class for {@link ValueExpression} implementations of the Fold
 * operation.
 * <p>
 * Fold has three operands: <code>values</code> (a {@link ValueExpression}),
 * <code>reducer</code> (a {@link BinaryOperator}) and <code>initial</code> (a
 * {@link ValueExpression}). First <code>initial</code> is evaluated. If it
 * does not return a single value, the final result is an empty list. Next,
 * <code>values</code> is evaluated and its result is passed to the abstract
 * {@link #prepareValues(ImmutableList)} method. The returned list is prefixed
 * by the value returned by evaluating <code>initial</code>. On this list, the
 * <code>reducer</code> is applied to the first two values until a single
 * value remains, which is then returned.
 */
public abstract class Fold implements ValueExpression {

    public final ValueExpression values;
    public final BinaryOperator<ValueExpression> reducer;
    public final ValueExpression initial;

    public Fold(final ValueExpression values, final BinaryOperator<ValueExpression> reducer, final ValueExpression initial) {
        this.values = checkNotNull(values, "values");
        this.reducer = checkNotNull(reducer, "reducer");
        this.initial = initial;
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        final ImmutableList<Value> initialList = initial != null ? initial.eval(parseState, encoding) : new ImmutableList<>();
        if (initialList.size > 1 || initialList.head == NOT_A_VALUE) {
            return new ImmutableList<>();
        }
        final ImmutableList<Value> unpreparedValues = this.values.eval(parseState, encoding);
        if (unpreparedValues.isEmpty()) {
            return initialList;
        }
        if (containsNotAValue(unpreparedValues).computeResult()) {
            return ImmutableList.create(NOT_A_VALUE);
        }
        final ImmutableList<Value> valueList = prepareValues(unpreparedValues).add(initialList);
        return ImmutableList.create(fold(parseState, encoding, reducer, valueList.head, valueList.tail).computeResult());
    }

    private Trampoline<Value> fold(final ParseState parseState, final Encoding encoding, final BinaryOperator<ValueExpression> reducer, final Value head, final ImmutableList<Value> tail) {
        if (head == NOT_A_VALUE) {
            return complete(() -> NOT_A_VALUE);
        }
        if (tail.isEmpty()) {
            return complete(() -> head);
        }
        final ImmutableList<Value> reducedValue = reduce(reducer, head, tail.head).eval(parseState, encoding);
        if (reducedValue.size != 1) {
            throw new IllegalArgumentException("Reducer must evaluate to a single value.");
        }
        return intermediate(() -> fold(parseState, encoding, reducer, reducedValue.head, tail.tail));
    }

    private Trampoline<Boolean> containsNotAValue(final ImmutableList<Value> list) {
        if (list.isEmpty()) {
            return complete(() -> false);
        }
        if (list.head != NOT_A_VALUE) {
            return intermediate(() -> containsNotAValue(list.tail));
        }
        return complete(() -> true);
    }

    protected abstract ImmutableList<Value> prepareValues(ImmutableList<Value> valueList);

    protected abstract ValueExpression reduce(BinaryOperator<ValueExpression> reducer, Value head, Value tail);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + values + "," + reducer + (initial == null ? "" : "," + initial) + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(values, ((Fold)obj).values)
            && Objects.equals(reducer, ((Fold)obj).reducer)
            && Objects.equals(initial, ((Fold)obj).initial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), values, reducer, initial);
    }

}
