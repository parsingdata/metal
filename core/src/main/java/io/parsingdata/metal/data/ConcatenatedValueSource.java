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

import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;

import java.math.BigInteger;
import java.util.Objects;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.expression.value.Value;

public class ConcatenatedValueSource extends Source {

    public final Value left;
    public final Value right;

    public ConcatenatedValueSource(final Value left, final Value right) {
        this.left = checkNotNull(left, "left");
        this.right = checkNotNull(right, "right");
    }

    @Override
    protected byte[] getData(final BigInteger offset, final BigInteger length) {
        if (!isAvailable(offset, length)) {
            throw new IllegalStateException("Data to read is not available ([offset=" + offset + ";length=" + length + ";source=" + this + ").");
        }
        final byte[] outputData = new byte[length.intValueExact()];
        if (offset.add(length).compareTo(left.getLength()) <= 0) {
            System.arraycopy(left.getValue(), offset.intValueExact(), outputData, 0, length.intValueExact());
        } else if (offset.compareTo(left.getLength()) >= 0) {
            System.arraycopy(right.getValue(), offset.subtract(left.getLength()).intValueExact(), outputData, 0, length.intValueExact());
        } else {
            final BigInteger leftPartLength = left.getLength().subtract(offset);
            System.arraycopy(left.getValue(), offset.intValueExact(), outputData, 0, leftPartLength.intValueExact());
            System.arraycopy(right.getValue(), 0, outputData, leftPartLength.intValueExact(), length.subtract(leftPartLength).intValueExact());
        }
        return outputData;
    }

    @Override
    protected boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return checkNotNegative(length, "length").add(checkNotNegative(offset, "offset")).compareTo(left.getLength().add(right.getLength())) <= 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + left + "," + right + "))";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(left, ((ConcatenatedValueSource)obj).left)
            && Objects.equals(right, ((ConcatenatedValueSource)obj).right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), left, right);
    }

}
