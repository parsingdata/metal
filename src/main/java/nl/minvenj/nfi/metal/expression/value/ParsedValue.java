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

public class ParsedValue extends Value {
    
    public static final String SEPARATOR = ".";
    
    private final String _scope;
    private final String _name;
    private final long _offset;
    
    public ParsedValue(final String scope, final String name, final long offset, final byte[] data, final Encoding enc) {
        super(data, enc);
        _scope = scope;
        _name = name;
        _offset = offset;
    }

    public String getScope() {
        return _scope;
    }

    public String getName() {
        return _name;
    }

    public String getFullName() {
        return getScope() + SEPARATOR + getName();
    }

    public boolean matches(final String name) {
        return getFullName().equals(name) || getFullName().endsWith(SEPARATOR + name);
    }
    
    public long getOffset() {
        return _offset;
    }
    
    @Override
    public String toString() {
        return getName() + ":" + super.toString();
    }
    
}
