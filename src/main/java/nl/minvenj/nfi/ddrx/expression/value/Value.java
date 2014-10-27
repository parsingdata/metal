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
import java.util.BitSet;

import javax.xml.bind.DatatypeConverter;

import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class Value {

    private final byte[] _data;
    private final Encoding _enc;

    public Value(final byte[] data, final Encoding enc) {
        _data = data;
        _enc = enc;
    }

    public byte[] getValue() {
        return _data;
    }

    public BigInteger asNumeric() {
        return _enc.isSigned() ? new BigInteger(_enc.getByteOrder().apply(_data))
                               : new BigInteger(1, _enc.getByteOrder().apply(_data));
    }

    public String asString() {
        return new String(_data, _enc.getCharset());
    }

    public BitSet asBitSet() {
        final byte[] data = _enc.getByteOrder().apply(_data);
        final BitSet bitSet = new BitSet(data.length * 8);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < 8; j++) {
                if (((data[i] >> j) & 1) == 1) {
                    bitSet.set((i * 8) + j);
                }
            }
        }
        return bitSet;
    }

    public Encoding getEncoding() {
        return _enc;
    }

    public OptionalValue operation(final ValueOperation op) {
        return op.execute(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + DatatypeConverter.printHexBinary(_data) + ")";
    }

}
