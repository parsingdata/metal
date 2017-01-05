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
 * Base class for {@link ValueExpression} implementations of the Fold
 * operation.
 * <p>
 * Fold has three operands: <code>values</code> (a {@link ValueExpression}),
 * <code>reducer</code> (a {@link Reducer}) and <code>initial</code> (a
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
    public final Reducer reducer;
    public final ValueExpression initial;

    public Fold(final ValueExpression values, final Reducer reducer, final ValueExpression initial) {
        this.values = checkNotNull(values, "values");
        this.reducer = checkNotNull(reducer, "reducer");
        this.initial = initial;
    }

    @Override
    public ImmutableList<OptionalValue> eval(final Environment environment, final Encoding encoding) {
        final ImmutableList<OptionalValue> initial = this.initial != null ? this.initial.eval(environment, encoding) : new ImmutableList<OptionalValue>();
        if (initial.size > 1) { return new ImmutableList<>(); }
        final ImmutableList<OptionalValue> values = prepareValues(this.values.eval(environment, encoding));
        if (values.isEmpty() || containsEmpty(values)) { return initial; }
        if (!initial.isEmpty()) {
            return ImmutableList.create(fold(environment, encoding, reducer, initial.head, values));
        }
        return ImmutableList.create(fold(environment, encoding, reducer, values.head, values.tail));
    }

    private OptionalValue fold(final Environment environment, final Encoding encoding, final Reducer reducer, final OptionalValue head, final ImmutableList<OptionalValue> tail) {
        if (!head.isPresent() || tail.isEmpty()) { return head; }
        final ImmutableList<OptionalValue> reducedValue = reduce(reducer, head.get(), tail.head.get()).eval(environment, encoding);
        if (reducedValue.size != 1) { throw new IllegalStateException("Reducer must yield a single value."); }
        return fold(environment, encoding, reducer, reducedValue.head, tail.tail);
    }

    private boolean containsEmpty(ImmutableList<OptionalValue> list) {
        if (list.isEmpty()) { return false; }
        return !list.head.isPresent() || containsEmpty(list.tail);
    }

    protected abstract ImmutableList<OptionalValue> prepareValues(ImmutableList<OptionalValue> values);

    protected abstract ValueExpression reduce(Reducer reducer, Value head, Value tail);

}
