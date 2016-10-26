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

package io.parsingdata.metal.data;

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;

public class ParseValue extends Value implements ParseItem {

    public final String name;
    public Token definition;
    public final long offset;

    public ParseValue(final String name, final Token definition, final long offset, final byte[] data, final Encoding encoding) {
        super(data, encoding);
        this.name = checkNotNull(name, "name");
        this.definition = checkNotNull(definition, "definition");
        this.offset = offset;
    }

    public boolean matches(final String name) {
        return this.name.equals(name) || this.name.endsWith(Token.SEPARATOR + name);
    }

    public long getOffset() { return offset; }

    @Override public boolean isValue() { return true; }
    @Override public boolean isGraph() { return false; }
    @Override public boolean isReference() { return false; }
    @Override public ParseValue asValue() { return this; }
    @Override public ParseGraph asGraph() { throw new UnsupportedOperationException("Cannot convert ParseValue to ParseGraph."); }
    @Override public ParseReference asReference() { throw new UnsupportedOperationException("Cannot convert ParseValue to ParseReference."); }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return name + "(" + super.toString() + ")";
    }

}
