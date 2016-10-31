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

import io.parsingdata.metal.token.Token;

public class ParseReference implements ParseItem {

    public final long location;
    public final Token definition;

    public ParseReference(final long location, final Token definition) {
        this.location = location;
        this.definition = checkNotNull(definition, "definition");
    }

    public ParseItem resolve(final ParseGraph root) {
        return findItemAtOffset(getAllRoots(root, definition), location);
    }

    @Override public boolean isValue() { return false; }
    @Override public boolean isGraph() { return false; }
    @Override public boolean isReference() { return true; }
    @Override public ParseValue asValue() { throw new UnsupportedOperationException("Cannot convert ParseReference to ParseValue."); }
    @Override public ParseGraph asGraph() { throw new UnsupportedOperationException("Cannot convert ParseReference to ParseGraph."); }
    @Override public ParseReference asReference() { return this; }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return "ref(@" + location + ")";
    }

}
