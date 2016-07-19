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

import io.parsingdata.metal.Util;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;

import java.math.BigInteger;
import java.util.BitSet;

import static io.parsingdata.metal.Util.checkNotNull;

public class Value {

    private final byte[] data; // Private because array content is mutable.
    public final Encoding enc;

    public Value(final byte[] data, final Encoding enc) {
        this.data = data.clone();
        this.enc = checkNotNull(enc, "enc");
    }

    public byte[] getValue() {
        return data.clone();
    }

    public BigInteger asNumeric() {
        return enc.isSigned() ? new BigInteger(enc.getByteOrder().apply(data))
                              : new BigInteger(1, enc.getByteOrder().apply(data));
    }

    public String asString() {
        return new String(data, enc.getCharset());
    }

    public BitSet asBitSet() {
        return BitSet.valueOf(enc.getByteOrder() == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN.apply(data) : data);
    }

    public OptionalValue operation(final ValueOperation op) {
        return op.execute(this);
    }

    @Override
    public String toString() {
        return "0x" + Util.bytesToHexString(data);
    }

}
