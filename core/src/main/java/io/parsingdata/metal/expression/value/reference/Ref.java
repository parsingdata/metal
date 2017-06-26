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

import static io.parsingdata.metal.SafeTrampoline.complete;
import static io.parsingdata.metal.SafeTrampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.selection.ByPredicate.getAllValues;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

/**
 * A {@link ValueExpression} that represents all {@link Value}s in the parse
 * state that match a provided object. This class only has a private
 * constructor and instead must be instantiated through one of its factory
 * methods: {@link #nameRef} (to match on name) and {@link #tokenRef} (to
 * match on definition).
 * @param <T> The type of reference to match on.
 */
public class Ref<T> implements ValueExpression {

    public final T reference;
    public final BiPredicate<ParseValue, T> predicate;

    private Ref(final T reference, final BiPredicate<ParseValue, T> predicate) {
        this.reference = checkNotNull(reference, "reference");
        this.predicate = checkNotNull(predicate, "predicate");
    }

    public static Ref<String> nameRef(final String name) {
        return new Ref<>(name, ParseValue::matches);
    }

    public static Ref<Token> tokenRef(final Token definition) {
        return new Ref<>(definition, (value, reference) -> value.definition.equals(reference));
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseGraph graph, final Encoding encoding) {
        return wrap(getAllValues(graph, (value) -> predicate.test(value, reference)), new ImmutableList<Optional<Value>>()).computeResult();
    }

    private static <T, U extends T> SafeTrampoline<ImmutableList<Optional<T>>> wrap(final ImmutableList<U> input, final ImmutableList<Optional<T>> output) {
        if (input.isEmpty()) { return complete(() -> output); }
        return intermediate(() -> wrap(input.tail, output.add(Optional.of(input.head))));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + reference + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(reference, ((Ref)obj).reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

}
