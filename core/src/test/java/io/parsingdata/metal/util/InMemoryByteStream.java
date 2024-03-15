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

package io.parsingdata.metal.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ByteStream;

public class InMemoryByteStream implements ByteStream {

    private final byte[] data;

    public InMemoryByteStream(final byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] read(final BigInteger offset, final int length) throws IOException {
        if (!isAvailable(offset, BigInteger.valueOf(length))) { throw new IOException("Data to read is not available."); }
        byte[] data = new byte[length];
        System.arraycopy(this.data, offset.intValueExact(), data, 0, length);
        return data;
    }

    @Override
    public boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return offset.add(length).compareTo(BigInteger.valueOf(data.length)) <= 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + data.length + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Arrays.equals(data, ((InMemoryByteStream)obj).data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), Arrays.hashCode(data));
    }

}
