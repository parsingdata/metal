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

import static io.parsingdata.metal.SafeTrampoline.complete;
import static io.parsingdata.metal.SafeTrampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.transformation.Reversal.reverse;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
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
 * {@link #eval(Value, ParseGraph, Encoding)} must be implemented, handling
 * the case of evaluating one value. This base class takes care of evaluating
 * the operand and handling list semantics.
 *
 * @see BinaryValueExpression
 */
public abstract class UnaryValueExpression implements ValueExpression {

    public final ValueExpression operand;

    public UnaryValueExpression(final ValueExpression operand) {
        this.operand = checkNotNull(operand, "operand");
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseGraph graph, final Encoding encoding) {
        return reverse(eval(operand.eval(graph, encoding), graph, encoding, new ImmutableList<>()).computeResult());
    }

    private SafeTrampoline<ImmutableList<Optional<Value>>> eval(final ImmutableList<Optional<Value>> values, final ParseGraph graph, final Encoding encoding, final ImmutableList<Optional<Value>> result) {
        if (values.isEmpty()) { return complete(() -> result); }
        return intermediate(() -> eval(values.tail, graph, encoding, result.add(values.head.flatMap(value -> eval(value, graph, encoding)))));
    }

    public abstract Optional<Value> eval(final Value value, final ParseGraph graph, final Encoding encoding);

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
    public int hashCode() {
        return Objects.hash(operand);
    }

}
