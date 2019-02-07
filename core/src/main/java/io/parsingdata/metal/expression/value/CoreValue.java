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

package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Util.bytesToHexString;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Objects;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;

public class CoreValue implements Value {

    public static final Value NOT_A_VALUE = new CoreValue(Slice.createFromBytes(new byte[]{}), DEFAULT_ENCODING);
    public static final BigInteger TO_STRING_BYTE_COUNT = BigInteger.valueOf(4);

    private final Slice slice;
    private final Encoding encoding;

    public CoreValue(final Slice slice, final Encoding encoding) {
        this.slice = checkNotNull(slice, "slice");
        this.encoding = checkNotNull(encoding, "encoding");
    }

    @Override
    public Slice getSlice() {
        return slice;
    }

    @Override
    public Encoding getEncoding() {
        return encoding;
    }

    @Override
    public byte[] getValue() {
        return slice.getData();
    }

    @Override
    public BigInteger getLength() {
        return slice.length;
    }

    @Override
    public BigInteger asNumeric() {
        return encoding.sign == Sign.SIGNED ? new BigInteger(encoding.byteOrder.apply(getValue()))
                                            : new BigInteger(1, encoding.byteOrder.apply(getValue()));
    }

    @Override
    public String asString() {
        return new String(getValue(), encoding.charset);
    }

    @Override
    public BitSet asBitSet() {
        return BitSet.valueOf(encoding.byteOrder == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN.apply(getValue()) : getValue());
    }

    @Override
    public String toString() {
        return "0x" + bytesToHexString(slice.getData(TO_STRING_BYTE_COUNT)) + (getLength().compareTo(TO_STRING_BYTE_COUNT) > 0 ? "..." : "");
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(slice, ((CoreValue)obj).slice)
            && Objects.equals(encoding, ((CoreValue)obj).encoding);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), slice, encoding);
    }

}
