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

package io.parsingdata.metal.expression.value.reference;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Selection.NO_LIMIT;
import static io.parsingdata.metal.data.Selection.getAllValues;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

/**
 * A {@link ValueExpression} that represents all {@link Value}s in the parse
 * state that match a provided object. This class only has a private
 * constructor and instead must be instantiated through one of its subclasses:
 * {@link NameRef} (to match on name) and {@link DefinitionRef} (to match on
 * definition). A limit argument may be provided to specify an upper bound to
 * the amount of returned results.
 * @param <T> The type of reference to match on.
 */
public class Ref<T> implements ValueExpression {

    public final T reference;
    public final Predicate<ParseValue> predicate;
    public final ValueExpression limit;

    private Ref(final T reference, final Predicate<ParseValue> predicate, final ValueExpression limit) {
        this.reference = checkNotNull(reference, "reference");
        this.predicate = checkNotNull(predicate, "predicate");
        this.limit = limit;
    }

    public static class NameRef extends Ref<String> {
        public NameRef(final String reference) { this(reference, null); }
        public NameRef(final String reference, final ValueExpression limit) { super(reference, value -> value.matches(reference), limit); }
    }

    public static class DefinitionRef extends Ref<Token> {
        public DefinitionRef(final Token reference) { this(reference, null); }
        public DefinitionRef(final Token reference, final ValueExpression limit) { super(reference, value -> value.definition.equals(reference), limit); }
    }

    @Override
    public Optional<ImmutableList<Value>> eval(final ParseState parseState, final Encoding encoding) {
        if (limit == null) {
            return evalImpl(parseState, NO_LIMIT);
        }
        final ImmutableList<Value> evaluatedLimit = limit.eval(parseState, encoding)
            .filter(limitValue -> limitValue.size == 1)
            .orElseThrow(() -> new IllegalArgumentException("Limit must evaluate to a single non-empty value."));
        return evalImpl(parseState, evaluatedLimit.head.asNumeric().intValueExact());
    }

    private Optional<ImmutableList<Value>> evalImpl(final ParseState parseState, final int limit) {
        final ImmutableList<ParseValue> values = getAllValues(parseState.order, predicate, limit);
        if (values.isEmpty()) { return Optional.empty(); }
        return Optional.of(wrap(values, new ImmutableList<Value>()).computeResult());
    }

    private static <T, U extends T> Trampoline<ImmutableList<T>> wrap(final ImmutableList<U> input, final ImmutableList<T> output) {
        if (input.isEmpty()) {
            return complete(() -> output);
        }
        return intermediate(() -> wrap(input.tail, output.add(input.head)));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + reference + (limit == null ? "" : "," + limit) + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(reference, ((Ref)obj).reference)
            && Objects.equals(limit, ((Ref)obj).limit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), reference, limit);
    }

}
