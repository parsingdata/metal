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

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.data.Slice.createFromSource;

import java.math.BigInteger;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.data.ConcatenatedValueSource;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

public class CatList implements ValueExpression {

    public final ValueExpression operand;

    public CatList(final ValueExpression operand) {
        this.operand = operand;
    }

    @Override
    public ImmutableList<Optional<Value>> eval(ParseState parseState, Encoding encoding) {
        final ImmutableList<Value> values = unwrap(operand.eval(parseState, encoding), new ImmutableList<>()).computeResult();
        final BigInteger length = calculateTotalSize(values);
        return createFromSource(new ConcatenatedValueSource(values, length), ZERO, length)
            .map(source -> new ImmutableList<Optional<Value>>().add(Optional.of(new Value(source, encoding))))
            .orElseGet(ImmutableList::new);
    }

    private static <T, U extends T> Trampoline<ImmutableList<T>> unwrap(final ImmutableList<Optional<U>> input, final ImmutableList<T> output) {
        if (input.isEmpty()) {
            return complete(() -> output);
        }
        return input.head
            .map(value -> intermediate(() -> unwrap(input.tail, output.add(value))))
            .orElseGet(() -> intermediate(() -> unwrap(input.tail, output)));
    }

    private BigInteger calculateTotalSize(final ImmutableList<Value> values) {
        return calculateTotalSize(values, ZERO).computeResult();
    }

    private Trampoline<BigInteger> calculateTotalSize(final ImmutableList<Value> values, final BigInteger size) {
        if (values.isEmpty()) {
            return complete(() -> size);
        }
        return intermediate(() -> calculateTotalSize(values.tail, size.add(values.head.slice.length)));
    }

}
