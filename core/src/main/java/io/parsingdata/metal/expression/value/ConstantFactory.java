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

package io.parsingdata.metal.expression.value;

import java.math.BigInteger;
import java.util.BitSet;

import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;

public final class ConstantFactory {

    private ConstantFactory() {}

    public static Value createFromBytes(final byte[] value, final Encoding encoding) {
        return new CoreValue(Slice.createFromBytes(value), encoding);
    }

    public static Value createFromNumeric(final BigInteger value, final Encoding encoding) {
        return createFromBytes(compact(value.toByteArray(), encoding.sign == Sign.SIGNED), setToBigEndian(encoding));
    }

    public static Value createFromNumeric(final long value, final Encoding encoding) {
        return createFromNumeric(BigInteger.valueOf(value), encoding);
    }

    public static Value createFromString(final String value, final Encoding encoding) {
        return new CoreValue(Slice.createFromBytes(value.getBytes(encoding.charset)), encoding);
    }

    public static Value createFromBitSet(final BitSet value, final int minSize, final Encoding encoding) {
        final byte[] bytes = ByteOrder.LITTLE_ENDIAN.apply(value.toByteArray());
        final byte[] outBytes = new byte[Math.max(minSize, bytes.length)];
        System.arraycopy(bytes, 0, outBytes, outBytes.length - bytes.length, bytes.length);
        return new CoreValue(Slice.createFromBytes(outBytes), setToBigEndian(encoding));
    }

    private static Encoding setToBigEndian(final Encoding encoding) {
        return new Encoding(encoding.sign, encoding.charset, ByteOrder.BIG_ENDIAN);
    }

    private static byte[] compact(final byte[] data, final boolean signed) {
        if (signed || data.length < 2) {
            return data;
        }
        // Strip possible leading zero byte.
        if (data[0] == 0) {
            final byte[] outBytes = new byte[data.length - 1];
            System.arraycopy(data, 1, outBytes, 0, outBytes.length);
            return outBytes;
        }
        return data;
    }

}
