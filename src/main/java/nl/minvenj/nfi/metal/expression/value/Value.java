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

package nl.minvenj.nfi.metal.expression.value;

import java.math.BigInteger;
import java.util.BitSet;

import javax.xml.bind.DatatypeConverter;

import nl.minvenj.nfi.metal.encoding.ByteOrder;
import nl.minvenj.nfi.metal.encoding.Encoding;

public class Value {

    private final byte[] _data;
    private final Encoding _enc;

    public Value(final byte[] data, final Encoding enc) {
        _data = data;
        if (enc == null) { throw new IllegalArgumentException("Argument enc may not be null."); }
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
        return BitSet.valueOf(_enc.getByteOrder() == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN.apply(_data) : _data);
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
