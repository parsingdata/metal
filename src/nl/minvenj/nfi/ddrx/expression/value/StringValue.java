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

import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class StringValue extends Value {

    private final String _stringValue;

    public StringValue(String name, byte[] value, Encoding encoding) {
        super(name, value, encoding);
        _stringValue = new String(_data, encoding.getCharset());
    }

    public StringValue(String value, Encoding encoding) {
        super("", value.getBytes(encoding.getCharset()), encoding);
        _stringValue = value;
    }

    public StringValue operation(StringValueOperation op) {
        return op.execute(getStringValue());
    }

    @Override
    public int compareTo(Value other) {
        if (other instanceof StringValue) {
            return getStringValue().compareTo(((StringValue) other).getStringValue());
        }
        return super.compareTo(other);
    }

    public String getStringValue() {
        return _stringValue;
    }

}
