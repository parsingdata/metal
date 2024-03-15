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

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Selection.findItemAtOffset;
import static io.parsingdata.metal.data.Selection.getAllRoots;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.token.Token;

public class ParseReference extends ImmutableObject implements ParseItem {

    public final BigInteger location;
    public final Source source;
    public final Token definition;

    public ParseReference(final BigInteger location, final Source source, final Token definition) {
        this.location = checkNotNull(location, "location");
        this.source = checkNotNull(source, "source");
        this.definition = checkNotNull(definition, "definition");
    }

    public Optional<ParseItem> resolve(final ParseGraph root) {
        return findItemAtOffset(getAllRoots(root, definition), location, source).computeResult();
    }

    @Override public boolean isReference() { return true; }
    @Override public ParseReference asReference() { return this; }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return "pref(@" + location + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(location, ((ParseReference)obj).location)
            && Objects.equals(source, ((ParseReference)obj).source)
            && Objects.equals(definition, ((ParseReference)obj).definition);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), location, source, definition);
    }

}
