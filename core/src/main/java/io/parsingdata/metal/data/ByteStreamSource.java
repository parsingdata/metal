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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.Objects;

import io.parsingdata.metal.Util;

public class ByteStreamSource extends Source {

    public final ByteStream input;

    ByteStreamSource(final ByteStream input) {
        this.input = checkNotNull(input, "input");
    }

    @Override
    protected byte[] getData(final BigInteger offset, final BigInteger length) {
        if (!isAvailable(checkNotNegative(offset, "offset"), checkNotNegative(length, "length"))) {
            throw new IllegalStateException("Data to read is not available ([offset=" + offset + ";length=" + length + ";source=" + this + ").");
        }
        try {
            return input.read(offset, length.intValueExact());
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    protected boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return input.isAvailable(checkNotNegative(offset, "offset"), checkNotNegative(length, "length").intValueExact());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + input.toString() + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(input, ((ByteStreamSource)obj).input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), input);
    }

}
