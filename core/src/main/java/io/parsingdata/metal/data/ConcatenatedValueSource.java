/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.format;
import static io.parsingdata.metal.data.Selection.reverse;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.expression.value.Value;

public class ConcatenatedValueSource extends Source {

    public final ImmutableList<Value> values;
    public final BigInteger length;

    private ConcatenatedValueSource(final ImmutableList<Value> values, final BigInteger length) {
        this.values = checkNotNull(values, "values");
        this.length = checkNotNegative(length, "length");
    }

    public static Optional<ConcatenatedValueSource> create(final ImmutableList<Value> inputValues) {
        final ImmutableList<Value> values = reverse(inputValues);
        final BigInteger length = calculateTotalSize(values);
        if (length.compareTo(ZERO) == 0) {
            return Optional.empty();
        }
        return Optional.of(new ConcatenatedValueSource(values, length));
    }

    private static BigInteger calculateTotalSize(final ImmutableList<Value> values) {
        return calculateTotalSize(values, ZERO).computeResult();
    }

    private static Trampoline<BigInteger> calculateTotalSize(final ImmutableList<Value> values, final BigInteger size) {
        if (values.isEmpty()) {
            return complete(() -> size);
        }
        if (values.head().equals(NOT_A_VALUE)) {
            return complete(() -> ZERO);
        }
        return intermediate(() -> calculateTotalSize(values.tail(), size.add(values.head().slice().length)));
    }

    @Override
    protected byte[] getData(final BigInteger offset, final BigInteger length) {
        if (!isAvailable(offset, length)) {
            throw new IllegalStateException(format("Data to read is not available (offset=%d;length=%d;source=%s).", offset, length, this));
        }
        return getData(values, ZERO, ZERO, offset, length, new byte[length.intValueExact()]).computeResult();
    }

    private Trampoline<byte[]> getData(final ImmutableList<Value> values, final BigInteger currentOffset, final BigInteger currentDest, final BigInteger offset, final BigInteger length, final byte[] output) {
        if (length.compareTo(ZERO) <= 0) {
            return complete(() -> output);
        }
        final BigInteger nextOffset = currentOffset.add(values.head().slice().length);
        if (nextOffset.compareTo(offset) <= 0) {
            return intermediate(() -> getData(values.tail(), nextOffset, currentDest, offset, length, output));
        }
        final BigInteger localOffset = offset.subtract(currentOffset).compareTo(ZERO) < 0 ? ZERO : offset.subtract(currentOffset);
        // The second argument in getData in Slice is a limit. It will return less if the end of slice is reached.
        final byte[] data = values.head().slice().getData(localOffset, length);
        System.arraycopy(data, 0, output, currentDest.intValueExact(), data.length);
        return intermediate(() -> getData(values.tail(), nextOffset, currentDest.add(valueOf(data.length)), offset, length.subtract(valueOf(data.length)), output));
    }

    @Override
    protected boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return checkNotNegative(length, "length").add(checkNotNegative(offset, "offset")).compareTo(this.length) <= 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + values + "(" + length + "))";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(values, ((ConcatenatedValueSource)obj).values)
            && Objects.equals(length, ((ConcatenatedValueSource)obj).length);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), values, length);
    }

}
