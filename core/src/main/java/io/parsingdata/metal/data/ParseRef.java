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

import io.parsingdata.metal.data.selection.ByOffset;
import io.parsingdata.metal.token.Token;

public class ParseRef implements ParseItem {

    public final long location;
    public final Token definition;

    public ParseRef(final long location, final Token definition) {
        this.location = location;
        this.definition = checkNotNull(definition, "definition");
    }

    public ParseGraph resolve(final ParseGraph root) {
        return ByOffset.findRef(ParseGraphList.create(root).add(root.getGraphs()), location);
    }

    @Override public boolean isValue() { return false; }
    @Override public boolean isGraph() { return false; }
    @Override public boolean isRef() { return true; }
    @Override public ParseValue asValue() { throw new UnsupportedOperationException("Cannot convert ParseRef to ParseValue."); }
    @Override public ParseGraph asGraph() { throw new UnsupportedOperationException("Cannot convert ParseRef to ParseGraph."); }
    @Override public ParseRef asRef() { return this; }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return "ParseRef(" + location + ")";
    }

}
