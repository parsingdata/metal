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

package io.parsingdata.metal.data.selection;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.token.Token;

public class ByToken {

    private ByToken() {}

    public static ParseItem get(final ParseGraph graph, final Token definition) {
        if (definition == null) { throw new IllegalArgumentException("Argument definition may not be null."); }
        if (graph.definition == definition) { return graph; }
        if (graph.isEmpty()) { return null; }
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().definition == definition) { return head; }
        if (head.isGraph()) {
            final ParseItem item = get(head.asGraph(), definition);
            if (item != null) { return item; }
        }
        return get(graph.tail, definition);
    }

    public static ParseItemList getAll(final ParseGraph graph, final Token definition) {
        if (definition == null) { throw new IllegalArgumentException("Argument definition may not be null."); }
        return getAllRecursive(graph, definition);
    }

    private static ParseItemList getAllRecursive(final ParseGraph graph, final Token definition) {
        if (graph.isEmpty()) { return ParseItemList.EMPTY; }
        final ParseItemList tailResults = getAllRecursive(graph.tail, definition);
        final ParseItemList results = graph.definition == definition ? tailResults.add(graph) : tailResults;
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().definition == definition) { return results.add(head); }
        if (head.isGraph()) { return results.add(getAllRecursive(head.asGraph(), definition)); }
        return results;
    }

}
