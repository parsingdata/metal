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

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;

public class Slice extends ImmutableObject {

    public final Source source;
    public final BigInteger offset;
    public final BigInteger length;

    private Slice(final Source source, final BigInteger offset, final BigInteger length) {
        this.source = checkNotNull(source, "source");
        this.offset = checkNotNull(offset, "offset");
        this.length = checkNotNull(length, "length");
    }

    public static Optional<Slice> createFromSource(final Source source, final BigInteger offset, final BigInteger length) {
        if (checkNotNull(offset, "offset").compareTo(ZERO) < 0 ||
            checkNotNull(length, "length").compareTo(ZERO) < 0 ||
            !checkNotNull(source, "source").isAvailable(offset, length)) {
            return Optional.empty();
        }
        return Optional.of(new Slice(source, offset, length));
    }

    public static Slice createFromBytes(final byte[] data) {
        return new Slice(new ConstantSource(checkNotNull(data, "data")), ZERO, BigInteger.valueOf(data.length));
    }

    public byte[] getData() {
        return getData(length);
    }

    public byte[] getData(final BigInteger limit) {
        return getData(ZERO, limit);
    }

    /**
     * Return a part of the data specified by the offset and limit.
     * @param offset the offset to start reading the slice from
     * @param limit the maximum number of bytes returned. Fewer bytes are returned if the end of slice is reached.
     * @return a byte array representing the data.
     */
    public byte[] getData(final BigInteger offset, final BigInteger limit) {
        final BigInteger calculatedOffset = checkNotNegative(offset, "offset").add(this.offset);
        final BigInteger calculatedLength = checkNotNegative(limit, "limit").min(length.subtract(offset)).max(ZERO);
        return source.getData(calculatedOffset, calculatedLength);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + source + "@" + offset + ":" + length.add(offset) + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(source, ((Slice)obj).source)
            && Objects.equals(offset, ((Slice)obj).offset)
            && Objects.equals(length, ((Slice)obj).length);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), source, offset, length);
    }

}
