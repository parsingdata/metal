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

    public static Value createFromNumeric(BigInteger value, Encoding encoding) {
        return new Value(compact(value.toByteArray(), encoding.getByteOrder()), encoding);
    }

    public static Value createFromNumeric(long value, Encoding encoding) {
        return createFromNumeric(BigInteger.valueOf(value), encoding);
    }

    public static Value createFromString(String value, Encoding encoding) {
        return new Value(value.getBytes(encoding.getCharset()), encoding);
    }

    private static byte[] compact(byte[] in, ByteOrder byteOrder) {
        if (in.length < 2) {
            return in;
        }
        // strip leading zero bytes
        int i = 0;
        for (; i < in.length && in[i] == 0; i++);
        byte[] out = new byte[in.length - i];
        System.arraycopy(in, i, out, 0, out.length);
        
        // little endian: reverse array
        return (byteOrder == ByteOrder.LITTLE_ENDIAN) ? reverse(out) : out;
    }
    
    public static byte[] reverse(byte[] in) {
        int length = in.length;
        byte[] out = new byte[length];
        for (int i = 0; i <= length / 2; i++) {
            int other = length - 1 - i;
            out[i] = in[other]; // move back to front
            out[other] = in[i]; // move front to back
        }
        return out;
    }

}
