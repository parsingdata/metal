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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.expression.value.ConstantFactory.createFromNumeric;

import java.math.BigInteger;
import java.util.Optional;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ValueExpression} that represents the current index in an iteration
 * (e.g. when inside a {@link io.parsingdata.metal.token.Rep} or
 * {@link io.parsingdata.metal.token.RepN}).
 */
public class CurrentIndex implements ValueExpression {

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        final Optional<ParseGraph> result = findIterable(parseState.order);
        if (!result.isPresent()) { return ImmutableList.create(Optional.empty()); }
        return ImmutableList.create(Optional.of(createFromNumeric(countIterables(result.get(), ZERO), new Encoding())));
    }

    private Optional<ParseGraph> findIterable(final ParseItem item) {
        if (!item.isGraph() || item.asGraph().isEmpty()) { return Optional.empty(); }
        if (!item.getDefinition().isIterable()) { return findIterable(item.asGraph().head); }
        return Optional.of(item.asGraph());
    }

    private BigInteger countIterables(final ParseGraph graph, final BigInteger count) {
        if (graph.isEmpty()) { return count.subtract(ONE); }
        if (graph.tail.getDefinition().equals(graph.getDefinition())) { return countIterables(graph.tail, count.add(ONE)); }
        return count.subtract(ONE);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
