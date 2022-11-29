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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.format;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import java.math.BigInteger;
import java.util.Objects;

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
            throw new IllegalStateException(format("Data to read is not available ([offset=%d;length=%d;source=%s).", offset, length, this));
        }
        final byte[] outputData = new byte[length.intValueExact()];
        System.arraycopy(data, offset.intValueExact(), outputData, 0, outputData.length);
        return outputData;
    }

    @Override
    protected boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return checkNotNegative(offset, "offset").add(checkNotNegative(length, "length")).compareTo(BigInteger.valueOf(getValue().length)) <= 0;
    }

    @Override
    public BigInteger size() {
        return BigInteger.valueOf(getValue().length);
    }

    private synchronized byte[] getValue() {
        if (cache == null) {
            final ImmutableList<Value> results = dataExpression.eval(parseState, encoding);
            if (results.size <= index) {
                throw new IllegalStateException(format("ValueExpression dataExpression yields %d result(s) (expected at least %d).", results.size, index+1));
            }
            final Value cacheValue = getValueAtIndex(results, index, 0).computeResult();
            if (cacheValue.equals(NOT_A_VALUE)) {
                throw new IllegalStateException(format("ValueExpression dataExpression yields NOT_A_VALUE at index %d.", index));
            }
            cache = cacheValue.value();
        }
        return cache;
    }

    private Trampoline<Value> getValueAtIndex(final ImmutableList<Value> results, final int index, final int current) {
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
