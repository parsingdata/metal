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

package io.parsingdata.metal.expression.value;

import static java.math.BigInteger.ONE;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Slice.createFromSource;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import java.math.BigInteger;
import java.util.Objects;

import io.parsingdata.metal.ImmutableObject;
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
public class Bytes extends ImmutableObject implements ValueExpression {

    public final ValueExpression operand;

    public Bytes(final ValueExpression operand) {
        this.operand = checkNotNull(operand, "operand");
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        final ImmutableList<Value> values = operand.eval(parseState, encoding);
        return values.isEmpty() ? values : toByteValues(new ImmutableList<>(), values.head(), values.tail(), encoding).computeResult();
    }

    private Trampoline<ImmutableList<Value>> toByteValues(final ImmutableList<Value> output, final Value head, final ImmutableList<Value> tail, final Encoding encoding) {
        final ImmutableList<Value> result = output.addList(extractByteValues(new ImmutableList<>(), head, 0, encoding).computeResult());
        if (tail.isEmpty()) {
            return complete(() -> result);
        } else {
            return intermediate(() -> toByteValues(result, tail.head(), tail.tail(), encoding));
        }
    }

    private Trampoline<ImmutableList<Value>> extractByteValues(final ImmutableList<Value> output, final Value value, final int i, final Encoding encoding) {
        if (value.equals(NOT_A_VALUE) || BigInteger.valueOf(i).compareTo(value.length()) >= 0) {
            return complete(() -> output);
        }
        return intermediate(() -> extractByteValues(output.addHead(new CoreValue(createFromSource(value.slice().source, value.slice().offset.add(BigInteger.valueOf(i)), ONE).get(), encoding)), value, i + 1, encoding));
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
    public int immutableHashCode() {
        return Objects.hash(getClass(), operand);
    }

}
