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

package nl.minvenj.nfi.ddrx.expression.value;

import java.math.BigInteger;

import nl.minvenj.nfi.ddrx.encoding.ByteOrder;
import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class ConstantFactory {

    public static Value createFromNumeric(final BigInteger value, final Encoding enc) {
        return new Value(compact(value.toByteArray()), new Encoding(enc.isSigned(), enc.getCharset(), ByteOrder.BIG_ENDIAN));
    }

    public static Value createFromNumeric(final long value, final Encoding enc) {
        return createFromNumeric(BigInteger.valueOf(value), enc);
    }

    public static Value createFromString(final String value, final Encoding encoding) {
        return new Value(value.getBytes(encoding.getCharset()), encoding);
    }

    private static byte[] compact(final byte[] in) {
        if (in.length < 2) {
            return in;
        }
        // strip leading zero bytes
        int i = 0;
        for (; i < in.length && in[i] == 0; i++);
        final byte[] out = new byte[in.length - i];
        System.arraycopy(in, i, out, 0, out.length);
        return out;
    }

}
