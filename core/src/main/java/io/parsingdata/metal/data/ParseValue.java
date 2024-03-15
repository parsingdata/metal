/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static io.parsingdata.metal.Util.checkNotEmpty;
import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Objects;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.token.Token;

public class ParseValue extends CoreValue implements ParseItem {

    public final String name;
    public final Token definition;

    public ParseValue(final String name, final Token definition, final Slice slice, final Encoding encoding) {
        super(slice, encoding);
        this.name = checkNotEmpty(name, "name");
        this.definition = checkNotNull(definition, "definition");
    }

    public boolean matches(final String name) {
        return this.name.equals(name) || this.name.endsWith(Token.SEPARATOR + name);
    }

    public boolean matches(final Token definition) {
        return this.definition.equals(definition);
    }

    @Override public boolean isValue() { return true; }
    @Override public ParseValue asValue() { return this; }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return "pval(" + name + ":" + super.toString() + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(name, ((ParseValue)obj).name)
            && Objects.equals(definition, ((ParseValue)obj).definition);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(super.immutableHashCode(), name, definition);
    }

}
