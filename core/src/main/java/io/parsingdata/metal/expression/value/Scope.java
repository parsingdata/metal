/*
 * Copyright 2013-2019 Netherlands Forensic Institute
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

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import java.util.Objects;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

public class Scope implements ValueExpression {

    public final ValueExpression scopedValueExpression;
    public final SingleValueExpression scopeSize;

    public Scope(final ValueExpression scopedValueExpression, final SingleValueExpression scopeSize) {
        this.scopedValueExpression = checkNotNull(scopedValueExpression, "scopedValueExpression");
        this.scopeSize = checkNotNull(scopeSize, "scopeSize");
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        return scopeSize.evalSingle(parseState, encoding)
            .filter(sizeValue -> !sizeValue.equals(NOT_A_VALUE) && sizeValue.asNumeric().compareTo(ZERO) >= 0)
            .map(sizeValue -> scopedValueExpression.eval(parseState.withOrder(calculateScope(parseState.order, sizeValue.asNumeric().intValueExact())), encoding))
            .orElseThrow(() -> new IllegalArgumentException("Argument scopeSize must evaluate to a positive, non-empty countable value."));

    }

    private ParseGraph calculateScope(final ParseGraph order, final int size) {
        final ImmutableList<ParseGraph> scopeList = createScopeList(order, new ImmutableList<>()).computeResult();
        if (size >= scopeList.size) { return order; }
        return findScope(scopeList, size).computeResult();
    }

    private Trampoline<ImmutableList<ParseGraph>> createScopeList(final ParseGraph order, final ImmutableList<ParseGraph> list) {
        final ImmutableList<ParseGraph> newList = order.getDefinition().isScopeDelimiter() ? list.add(order) : list;
        if (!order.head.isGraph()) { return complete(() -> list); }
        return intermediate(() -> createScopeList(order.head.asGraph(), newList));
    }

    private Trampoline<ParseGraph> findScope(final ImmutableList<ParseGraph> scopeList, final int size) {
        if (size == 0) { return complete(() -> scopeList.head); }
        return intermediate(() -> findScope(scopeList.tail, size - 1));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + scopedValueExpression + "," + scopeSize + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(scopedValueExpression, ((Scope)obj).scopedValueExpression)
            && Objects.equals(scopeSize, ((Scope)obj).scopeSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), scopedValueExpression, scopeSize);
    }

}
