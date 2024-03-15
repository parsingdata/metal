/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkContainsNoNulls;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.ImmutableList.create;
import static io.parsingdata.metal.data.Selection.NO_LIMIT;
import static io.parsingdata.metal.data.Selection.getAllValues;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.SingleValueExpression;
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
public abstract class Ref<T> extends ImmutableObject implements ValueExpression {

    public final ImmutableList<T> references;
    public final BiPredicate<ParseValue, T> predicate;
    public final SingleValueExpression limit;
    public final SingleValueExpression scope;

    @SafeVarargs
    private Ref(final BiPredicate<ParseValue, T> predicate, final SingleValueExpression limit, final SingleValueExpression scope, final T reference,  final T... references) {
        this.predicate = checkNotNull(predicate, "predicate");
        this.limit = limit;
        this.scope = scope;
        this.references = create(checkContainsNoNulls(references, "references"))
            .addHead(checkNotNull(reference, "reference"));
    }

    private Ref(final BiPredicate<ParseValue, T> predicate, final SingleValueExpression limit, final SingleValueExpression scope, final ImmutableList<T> references) {
        this.predicate = checkNotNull(predicate, "predicate");
        this.limit = limit;
        this.scope = scope;
        this.references = checkNotNull(references, "references");
    }

    public static class NameRef extends Ref<String> {
        public NameRef(final String reference, final String... references) { this(null, null, reference, references); }
        public NameRef(final SingleValueExpression limit, final String reference, final String... references) { this(limit, null, reference, references); }
        public NameRef(final SingleValueExpression limit, final SingleValueExpression scope, final String reference, final String... references) { super(ParseValue::matches, limit, scope, reference, references); }
        private NameRef(final BiPredicate<ParseValue, String> predicate, final SingleValueExpression limit, final SingleValueExpression scope, final ImmutableList<String> references) { super(predicate, limit, scope, references); }

        @Override
        protected ImmutableList<Value> evalImpl(final ParseState parseState, final int limit, final int requestedScope) {
            return Optional.of(parseState.cache)
                .filter(p -> references.size() == 1)
                .filter(p -> requestedScope >= parseState.scopeDepth)
                .flatMap(p -> p.find(references.head(), limit))
                .orElseGet(() -> super.evalImpl(parseState, limit, requestedScope));
        }

        @Override
        public NameRef withLimit(final SingleValueExpression limit) {
            return new NameRef(predicate, limit, scope, references);
        }
        @Override
        public NameRef withScope(final SingleValueExpression scope) {
            return new NameRef(predicate, limit, scope, references);
        }
    }

    public static class DefinitionRef extends Ref<Token> {
        public DefinitionRef(final Token reference, final Token... references) { this(null, null, reference, references); }
        public DefinitionRef(final SingleValueExpression limit, final Token reference, final Token... references) { this(limit, null, reference, references); }
        public DefinitionRef(final SingleValueExpression limit, final SingleValueExpression scope, final Token reference, final Token... references) { super(ParseValue::matches, limit, scope, reference, references); }
        private DefinitionRef(final BiPredicate<ParseValue, Token> predicate, final SingleValueExpression limit, final SingleValueExpression scope, final ImmutableList<Token> references) { super(predicate, limit, scope, references); }

        @Override
        public DefinitionRef withLimit(final SingleValueExpression limit) {
            return new DefinitionRef(predicate, limit, scope, references);
        }
        @Override
        public DefinitionRef withScope(final SingleValueExpression scope) {
            return new DefinitionRef(predicate, limit, scope, references);
        }
    }

    public abstract Ref<T> withLimit(final SingleValueExpression limit);
    public abstract Ref<T> withScope(final SingleValueExpression scope);

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        final int requestedScope = scope == null ? parseState.scopeDepth : scope.evalSingle(parseState, encoding)
            .filter(sizeValue -> !sizeValue.equals(NOT_A_VALUE) && sizeValue.asNumeric().compareTo(ZERO) >= 0)
            .orElseThrow(() -> new IllegalArgumentException("Argument scopeSize must evaluate to a positive, countable value.")).asNumeric().intValueExact();
        if (limit == null) {
            return evalImpl(parseState, NO_LIMIT, requestedScope);
        }
        return limit.evalSingle(parseState, encoding)
            .map(limitValue -> limitValue.equals(NOT_A_VALUE) ? create(NOT_A_VALUE) : evalImpl(parseState, limitValue.asNumeric().intValueExact(), requestedScope))
            .orElseThrow(() -> new IllegalArgumentException("Limit must evaluate to a non-empty value."));
    }

    protected ImmutableList<Value> evalImpl(final ParseState parseState, final int limit, final int requestedScope) {
        return wrap(getAllValues(parseState.order, parseValue -> toList(references).stream().anyMatch(ref -> predicate.test(parseValue, ref)), limit, requestedScope, parseState.scopeDepth), new ImmutableList<Value>()).computeResult();
    }

    static <T> List<T> toList(final ImmutableList<T> allValues) {
        final List<T> flatten = new ArrayList<>();
        ImmutableList<T> tail = allValues;
        while (!tail.isEmpty()) {
            flatten.add(tail.head());
            tail = tail.tail();
        }
        return flatten;
    }

    private static <T, U extends T> Trampoline<ImmutableList<T>> wrap(final ImmutableList<U> input, final ImmutableList<T> output) {
        if (input.isEmpty()) {
            return complete(() -> output);
        }
        return intermediate(() -> wrap(input.tail(), output.addHead(input.head())));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + references + (limit == null ? "" : "," + limit) + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(references, ((Ref<?>)obj).references)
            && Objects.equals(limit, ((Ref<?>)obj).limit)
            && Objects.equals(scope, ((Ref<?>)obj).scope);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), references, limit, scope);
    }

}
