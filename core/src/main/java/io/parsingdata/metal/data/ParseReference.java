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
import static io.parsingdata.metal.data.selection.ByOffset.findItemAtOffset;
import static io.parsingdata.metal.data.selection.ByToken.getAllRoots;

import java.util.Objects;

import io.parsingdata.metal.token.Token;

public class ParseReference implements ParseItem {

    public final long location;
    public final Source source;
    public final Token definition;

    public ParseReference(final long location, final Source source, final Token definition) {
        this.location = location;
        this.source = checkNotNull(source, "source");
        this.definition = checkNotNull(definition, "definition");
    }

    public ParseItem resolve(final ParseGraph root) {
        return findItemAtOffset(getAllRoots(root, definition), location, source);
    }

    public boolean isReference() { return true; }
    public ParseReference asReference() { return this; }
    public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return "ref(@" + location + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
            && getClass() == obj.getClass()
            && Objects.equals(location, ((ParseReference)obj).location)
            && Objects.equals(source, ((ParseReference)obj).source)
            && Objects.equals(definition, ((ParseReference)obj).definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, source, definition);
    }

}
