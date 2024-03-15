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

import static java.util.stream.Collectors.toList;

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.LongStream;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ByteStream;

public class ReadTrackingByteStream implements ByteStream {

    private final ByteStream byteStream;
    private final Set<Long> read = new HashSet<>();

    public ReadTrackingByteStream(final ByteStream byteStream) {
        this.byteStream = checkNotNull(byteStream, "byteStream");
    }

    @Override
    public byte[] read(final BigInteger offset, final int length) throws IOException {
        for (long i = offset.longValueExact(); i < offset.longValueExact()+length; i++) {
            read.add(i);
        }
        return byteStream.read(offset, length);
    }

    public boolean containsAll(final long... values) {
        return read.containsAll(LongStream.of(values).boxed().collect(toList()));
    }

    public boolean containsNone(final long... values) {
        for (final long v : LongStream.of(values).boxed().collect(toList())) {
            if (read.contains(v)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return byteStream.isAvailable(offset, length);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + byteStream + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
                && Objects.equals(byteStream, ((ReadTrackingByteStream)obj).byteStream);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), Objects.hashCode(byteStream));
    }

}
