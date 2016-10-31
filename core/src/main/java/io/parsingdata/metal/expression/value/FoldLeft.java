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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.containsEmpty;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;

public class FoldLeft implements ValueExpression {

    public final ValueExpression values;
    public final Reducer reducer;
    public final ValueExpression initial;

    public FoldLeft(final ValueExpression values, final Reducer reducer, final ValueExpression initial) {
        this.values = checkNotNull(values, "values");
        this.reducer = checkNotNull(reducer, "reducer");
        this.initial = initial;
    }

    @Override
    public ImmutableList<OptionalValue> eval(final Environment environment, final Encoding encoding) {
        final ImmutableList<OptionalValue> initial = this.initial != null ? this.initial.eval(environment, encoding) : new ImmutableList<OptionalValue>();
        if (initial.size > 1) { return new ImmutableList<>(); }
        final ImmutableList<OptionalValue> values = this.values.eval(environment, encoding).reverse();
        if (values.isEmpty() || containsEmpty(values)) { return initial; }
        if (!initial.isEmpty()) {
            return ImmutableList.create(fold(environment, encoding, reducer, initial.head, values));
        }
        return ImmutableList.create(fold(environment, encoding, reducer, values.head, values.tail));
    }

    private OptionalValue fold(final Environment environment, final Encoding encoding, final Reducer reducer, final OptionalValue head, final ImmutableList<OptionalValue> tail) {
        if (!head.isPresent() || tail.isEmpty()) { return head; }
        final ImmutableList<OptionalValue> reducedValue = reducer.reduce(con(head.get()), con(tail.head.get())).eval(environment, encoding);
        if (reducedValue.size != 1) { throw new IllegalStateException("Reducer must yield a single value."); }
        return fold(environment, encoding, reducer, reducedValue.head, tail.tail);
    }

}
