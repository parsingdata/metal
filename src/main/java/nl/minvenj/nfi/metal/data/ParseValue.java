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

package nl.minvenj.nfi.metal.data;

import static nl.minvenj.nfi.metal.Util.checkNotNull;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.value.Value;
import nl.minvenj.nfi.metal.token.Token;

public class ParseValue extends Value implements ParseItem {

    public static final String SEPARATOR = ".";

    public final String scope;
    public final String name;
    public Token definition;
    public final long offset;

    public ParseValue(final String scope, final String name, final Token definition, final long offset, final byte[] data, final Encoding enc) {
        super(data, enc);
        this.scope = checkNotNull(scope, "scope");
        this.name = checkNotNull(name, "name");
        this.definition = checkNotNull(definition, "definition");
        this.offset = offset;
    }

    public String getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return getScope() + SEPARATOR + getName();
    }

    public boolean matches(final String name) {
        return getFullName().equals(name) || getFullName().endsWith(SEPARATOR + name);
    }

    public long getOffset() {
        return offset;
    }

    @Override public boolean isValue() { return true; }
    @Override public boolean isGraph() { return false; }
    @Override public boolean isRef() { return false; }
    @Override public ParseValue asValue() { return this; }
    @Override public ParseGraph asGraph() { throw new UnsupportedOperationException("Cannot convert ParseValue to ParseGraph."); }
    @Override public ParseRef asRef() { throw new UnsupportedOperationException("Cannot convert ParseValue to ParseRef."); }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return "ParseValue(" + getName() + ":" + super.toString() + ")";
    }

}
