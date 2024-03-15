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

package io.parsingdata.metal.data;

import static io.parsingdata.metal.Util.bytesToHexString;
import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.format;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import io.parsingdata.metal.Util;

public class ConstantSource extends Source {

    private final byte[] data; // Private because array content is mutable.

    public ConstantSource(final byte[] data) {
        this.data = checkNotNull(data, "data");
    }

    @Override
    protected byte[] getData(final BigInteger offset, final BigInteger length) {
        if (!isAvailable(offset, length)) {
            throw new IllegalStateException(format("Data to read is not available ([offset=%d;length=%d;source=%s).", offset, length, this));
        }
        final byte[] outputData = new byte[length.intValueExact()];
        System.arraycopy(data, offset.intValueExact(), outputData, 0, outputData.length);
        return outputData;
    }

    @Override
    protected boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return checkNotNegative(length, "length").add(checkNotNegative(offset, "offset")).compareTo(BigInteger.valueOf(data.length)) <= 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(0x" + bytesToHexString(data) + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Arrays.equals(data, ((ConstantSource)obj).data);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), Arrays.hashCode(data));
    }

}
