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

import static io.parsingdata.metal.Util.checkNotNegative;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.format;

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
        if (!isAvailable(offset, length)) {
            throw new IllegalStateException(format("Data to read is not available ([offset=%d;length=%d;source=%s).", offset, length, this));
        }
        try {
            return input.read(offset, length.intValueExact());
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    protected boolean isAvailable(final BigInteger offset, final BigInteger length) {
        return input.isAvailable(checkNotNegative(offset, "offset"), checkNotNegative(length, "length"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + input + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(input, ((ByteStreamSource)obj).input);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), input);
    }

}
