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

import java.math.BigInteger;
import java.util.BitSet;

import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;

public class ConstantFactory {

    public static Value createFromNumeric(final BigInteger value, final Encoding enc) {
        byte[] bytes = compact(value.toByteArray(), enc.isSigned());
        bytes = enc.getByteOrder().apply(bytes);
        return new Value(bytes, enc);
    }

    public static Value createFromNumeric(final long value, final Encoding enc) {
        return createFromNumeric(BigInteger.valueOf(value), enc);
    }

    public static Value createFromString(final String value, final Encoding enc) {
        return new Value(value.getBytes(enc.getCharset()), enc);
    }

    public static Value createFromBitSet(final BitSet value, final int minSize, final Encoding enc) {
        byte[] bytes = value.toByteArray();
        if (enc.getByteOrder() == ByteOrder.BIG_ENDIAN) {
            bytes = ByteOrder.LITTLE_ENDIAN.apply(bytes); // reverse bytes
        }
        final byte[] out = new byte[Math.max(minSize, bytes.length)];
        System.arraycopy(bytes, 0, out, out.length - bytes.length, bytes.length);
        return new Value(out, enc);
    }

    private static byte[] compact(final byte[] in, final boolean signed) {
        if (signed) { return in; }
        if (in.length < 2) { return in; }
        // strip leading zero bytes
        int i = 0;
        for (; i < in.length && in[i] == 0; i++);
        final byte[] out = new byte[in.length - i];
        System.arraycopy(in, i, out, 0, out.length);
        return out;
    }

}
