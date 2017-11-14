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

import static java.math.BigInteger.ONE;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Slice.createFromSource;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that splits the results of evaluating its operand
 * into individual bytes.
 * <p>
 * A Bytes expression has a single <code>operand</code> (a
 * {@link ValueExpression}). When evaluated, it evaluates <code>operand</code>
 * and instead of returning the list of results, each result is split into
 * {@link Value} objects representing each individual byte of the original
 * result.
 * <p>
 * For example, if <code>operand</code> evaluates to a list of two values, of
 * 2 and 3 bytes respectively, the Bytes expression turns this into a list of
 * 5 values, representing the individual bytes of the original results.
 */
public class Bytes implements ValueExpression {

    public final ValueExpression operand;

    public Bytes(final ValueExpression operand) {
        this.operand = checkNotNull(operand, "operand");
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        final ImmutableList<Optional<Value>> input = operand.eval(parseState, encoding);
        if (input.isEmpty()) {
            return input;
        }
        return toByteValues(new ImmutableList<>(), input.head, input.tail, encoding).computeResult();
    }

    private Trampoline<ImmutableList<Optional<Value>>> toByteValues(final ImmutableList<Optional<Value>> output, final Optional<Value> head, final ImmutableList<Optional<Value>> tail, final Encoding encoding) {
        final ImmutableList<Optional<Value>> result = output.add(
            head.map(value -> extractByteValues(new ImmutableList<>(), value, 0, encoding).computeResult())
                .orElseGet(ImmutableList::new));
        return tail.isEmpty() ? complete(() -> result) : intermediate(() -> toByteValues(result, tail.head, tail.tail, encoding));
    }

    private Trampoline<ImmutableList<Optional<Value>>> extractByteValues(final ImmutableList<Optional<Value>> output, final Value value, final int i, final Encoding encoding) {
        if (BigInteger.valueOf(i).compareTo(value.getLength()) >= 0) {
            return complete(() -> output);
        }
        return intermediate(() -> extractByteValues(output.add(Optional.of(new Value(createFromSource(value.slice.source, value.slice.offset.add(BigInteger.valueOf(i)), ONE).get(), encoding))), value, i + 1, encoding));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operand + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(operand, ((Bytes)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), operand);
    }

}
