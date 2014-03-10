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

import java.nio.charset.Charset;

public class StringValue extends Value {
    
    final static private Charset charset = Charset.forName("ISO646-US");
    
    public StringValue(String name, byte[] value) {
        super(name, value);
    }
    
    public StringValue(String value) {
        super("", value.getBytes(charset));
    }
    
    public StringValue operation(StringValueOperation op) {
        return op.execute(toString());
    }
    
    public int compareTo(Value other) {
        if (other instanceof StringValue) {
            return toString().compareTo(((StringValue)other).toString());
        }
        return super.compareTo(other);
    }
    
    @Override
    public String toString() {
        return new String(_data, charset);
    }
    
}
