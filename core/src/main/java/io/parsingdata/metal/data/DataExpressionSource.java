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
import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;
import java.math.BigInteger;
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
    public final ParseGraph graph;
    public final Encoding encoding;

    public DataExpressionSource(final ValueExpression dataExpression, final int index, final ParseGraph graph, final Encoding encoding) {
        this.dataExpression = checkNotNull(dataExpression, "dataExpression");
        this.index = index;
        this.graph = checkNotNull(graph, "graph");
        this.encoding = checkNotNull(encoding, "encoding");
    }

    @Override
    protected byte[] getData(final long offset, final BigInteger length) throws IOException {
        if (!isAvailable(offset, length)) { throw new IOException("Data to read is not available."); }
        final ImmutableList<Optional<Value>> results = dataExpression.eval(graph, encoding);
        final byte[] inputData = getValueAtIndex(results, index, 0).computeResult().get().getValue();
        final byte[] outputData = new byte[length.intValue()];
        System.arraycopy(inputData, (int)offset, outputData, 0, outputData.length);
        return outputData;
    }

    @Override
    public boolean isAvailable(final long offset, final BigInteger length) {
        final ImmutableList<Optional<Value>> results = dataExpression.eval(graph, encoding);
        if (results.size <= index) { throw new IllegalStateException("ValueExpression dataExpression yields " + results.size + " result(s) (expected at least " + (index + 1) + ")."); }
        return offset + length.intValue() <= getValueAtIndex(results, index, 0).computeResult().get().slice.length.intValue();
    }

    private Trampoline<Optional<Value>> getValueAtIndex(final ImmutableList<Optional<Value>> results, final int index, final int current) {
        if (index == current) { return complete(() -> results.head); }
        return intermediate(() -> getValueAtIndex(results.tail, index, current + 1));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + dataExpression.toString() + "[" + index + "](" + graph + "," + encoding + "))";
    }

    @Override
    public boolean equals(Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(dataExpression, ((DataExpressionSource)obj).dataExpression)
            && Objects.equals(index, ((DataExpressionSource)obj).index)
            && Objects.equals(graph, ((DataExpressionSource)obj).graph)
            && Objects.equals(encoding, ((DataExpressionSource)obj).encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().hashCode(), dataExpression, index, graph, encoding);
    }

}
