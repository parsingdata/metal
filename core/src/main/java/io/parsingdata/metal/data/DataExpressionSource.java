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

package io.parsingdata.metal.data;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class DataExpressionSource extends Source {

    public final ValueExpression dataExpression;
    public final int index;
    public final ParseState parseState;
    public final Encoding encoding;

    private byte[] cache = null;

    public DataExpressionSource(final ValueExpression dataExpression, final int index, final ParseState parseState, final Encoding encoding) {
        this.dataExpression = checkNotNull(dataExpression, "dataExpression");
        this.index = index;
        this.parseState = checkNotNull(parseState, "parseState");
        this.encoding = checkNotNull(encoding, "encoding");
    }

    @Override
    protected byte[] getData(final BigInteger offset, final BigInteger length) {
        checkNotNegative(offset, "offset");
        final byte[] data = getValue();
        if (checkNotNegative(length, "length").add(offset).compareTo(BigInteger.valueOf(data.length)) > 0) {
            throw new IllegalStateException("Data to read is not available ([offset=" + offset + ";length=" + length + ";source=" + this + ").");
        }
        final byte[] outputData = new byte[length.intValueExact()];
        System.arraycopy(data, offset.intValueExact(), outputData, 0, outputData.length);
        return outputData;
    }

    @Override
    protected boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return checkNotNegative(offset, "offset").add(checkNotNegative(length, "length")).compareTo(BigInteger.valueOf(getValue().length)) <= 0;
    }

    private synchronized byte[] getValue() {
        if (cache == null) {
            final ImmutableList<Optional<Value>> results = dataExpression.eval(parseState, encoding);
            if (results.size <= index) {
                throw new IllegalStateException("ValueExpression dataExpression yields " + results.size + " result(s) (expected at least " + (index + 1) + ").");
            }
            cache = getValueAtIndex(results, index, 0)
                .computeResult()
                .map(Value::getValue)
                .orElseThrow(() -> new IllegalStateException("ValueExpression dataExpression yields empty Value at index " + index + "."));
        }
        return Arrays.copyOf(cache, cache.length);
    }

    private Trampoline<Optional<Value>> getValueAtIndex(final ImmutableList<Optional<Value>> results, final int index, final int current) {
        if (index == current) {
            return complete(() -> results.head);
        }
        return intermediate(() -> getValueAtIndex(results.tail, index, current + 1));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + dataExpression + "[" + index + "](" + parseState + "," + encoding + "))";
    }

    @Override
    public boolean equals(Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(dataExpression, ((DataExpressionSource)obj).dataExpression)
            && Objects.equals(index, ((DataExpressionSource)obj).index)
            && Objects.equals(parseState, ((DataExpressionSource)obj).parseState)
            && Objects.equals(encoding, ((DataExpressionSource)obj).encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), dataExpression, index, parseState, encoding);
    }

}
