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

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.Objects;

import io.parsingdata.metal.Util;

public class Slice {

    public final Source source;
    public final long offset;
    public final BigInteger length;

    public Slice(final Source source, final long offset, final BigInteger length) {
        this.source = checkNotNull(source, "source");
        this.offset = offset;
        this.length = checkNotNull(length, "length");
    }

    public byte[] getData() {
        return getData(length);
    }

    public byte[] getData(final BigInteger limit) {
        if (limit.compareTo(BigInteger.ZERO) < 0) { throw new IllegalArgumentException("Argument limit may not be negative."); }
        final BigInteger calculatedLength = limit.compareTo(length) > 0 ? length : limit;
        try {
            return source.getData(offset, calculatedLength);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + source + "@" + offset + ":" + length.add(BigInteger.valueOf(offset)) + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(source, ((Slice)obj).source)
            && Objects.equals(offset, ((Slice)obj).offset)
            && Objects.equals(length, ((Slice)obj).length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().hashCode(), source, offset, length);
    }

}
