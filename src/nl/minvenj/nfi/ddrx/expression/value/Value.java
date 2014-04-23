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

import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class Value {

    public static final String DEFAULT_NAME = "CONSTANT_VALUE";

    protected final String _name;
    protected final byte[] _data;
    protected final Encoding _encoding;

    public Value(byte[] data, Encoding encoding) {
        this(DEFAULT_NAME, data, encoding);
    }

    public Value(String name, byte[] data, Encoding encoding) {
        _name = name;
        _data = data;
        _encoding = encoding;
    }

    public String getName() {
        return _name;
    }

    public byte[] getValue() {
        return _data;
    }

    public Encoding getEncoding() {
        return _encoding;
    }

    public BigInteger asNumeric() {
        return _encoding.isSigned() ? new BigInteger(_data) : new BigInteger(1, _data);
    }

    public String asString() {
        return new String(_data, _encoding.getCharset());
    }

    public Value operation(ValueOperation op) {
        return op.execute(_data);
    }

    @Override
    public String toString() {
        String val = "";
        for (byte b : _data) {
            if (val.length() > 0) { val += " "; }
            val += String.format("%02x", b);
        }
        return getClass().getSimpleName() + "(" + val + ")";
    }

}
