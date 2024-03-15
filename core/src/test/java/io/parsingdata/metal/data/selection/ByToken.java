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

package io.parsingdata.metal.data.selection;

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.token.Token;

public final class ByToken {

    private ByToken() {}

    public static ParseItem get(final ParseGraph graph, final Token definition) {
        checkNotNull(graph, "graph");
        checkNotNull(definition, "definition");
        if (graph.definition.equals(definition)) {
            return graph;
        }
        if (graph.isEmpty()) {
            return null;
        }
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().definition.equals(definition)) {
            return head;
        }
        if (head.isGraph()) {
            final ParseItem item = get(head.asGraph(), definition);
            if (item != null) {
                return item;
            }
        }
        return get(graph.tail, definition);
    }

    public static ImmutableList<ParseItem> getAll(final ParseGraph graph, final Token definition) {
        checkNotNull(graph, "graph");
        checkNotNull(definition, "definition");
        return getAllRecursive(graph, definition);
    }

    private static ImmutableList<ParseItem> getAllRecursive(final ParseGraph graph, final Token definition) {
        if (graph.isEmpty()) {
            return new ImmutableList<>();
        }
        final ImmutableList<ParseItem> tailResults = getAllRecursive(graph.tail, definition);
        final ImmutableList<ParseItem> results = graph.definition.equals(definition) ? tailResults.addHead(graph) : tailResults;
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().definition.equals(definition)) {
            return results.addHead(head);
        }
        if (head.isReference() && head.asReference().definition.equals(definition)) {
            return results.addHead(head);
        }
        if (head.isGraph()) {
            return results.addList(getAllRecursive(head.asGraph(), definition));
        }
        return results;
    }

}
