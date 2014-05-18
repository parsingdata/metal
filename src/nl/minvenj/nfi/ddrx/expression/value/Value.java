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

import javax.xml.bind.DatatypeConverter;

import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class Value {

    public static final String DEFAULT_NAME = "CONSTANT_VALUE";

    protected final String _name;
    protected final byte[] _data;
    protected final Encoding _enc;

    public Value(byte[] data, Encoding enc) {
        this(DEFAULT_NAME, data, enc);
    }

    public Value(String name, byte[] data, Encoding encoding) {
        _name = name;
        _data = data;
        _enc = encoding;
    }

    public String getName() {
        return _name;
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

    public Encoding getEncoding() {
        return _enc;
    }

    public Value operation(ValueOperation op) {
        return op.execute(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + DatatypeConverter.printHexBinary(_data) + ")";
    }

}
