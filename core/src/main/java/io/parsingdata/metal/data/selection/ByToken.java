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

import static io.parsingdata.metal.SafeTrampoline.complete;
import static io.parsingdata.metal.SafeTrampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.selection.ByPredicate.NO_LIMIT;
import static io.parsingdata.metal.data.transformation.Reversal.reverse;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Token;

public final class ByToken {

    private ByToken() {}

    public static ParseItem get(final ParseGraph graph, final Token definition) {
        checkNotNull(graph, "graph");
        checkNotNull(definition, "definition");
        if (graph.definition.equals(definition)) { return graph; }
        if (graph.isEmpty()) { return null; }
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().definition.equals(definition)) { return head; }
        if (head.isGraph()) {
            final ParseItem item = get(head.asGraph(), definition);
            if (item != null) { return item; }
        }
        return get(graph.tail, definition);
    }

    public static ImmutableList<ParseItem> getAll(final ParseGraph graph, final Token definition) {
        checkNotNull(graph, "graph");
        checkNotNull(definition, "definition");
        return getAllRecursive(graph, definition);
    }

    private static ImmutableList<ParseItem> getAllRecursive(final ParseGraph graph, final Token definition) {
        if (graph.isEmpty()) { return new ImmutableList<>(); }
        final ImmutableList<ParseItem> tailResults = getAllRecursive(graph.tail, definition);
        final ImmutableList<ParseItem> results = graph.definition.equals(definition) ? tailResults.add(graph) : tailResults;
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().definition.equals(definition)) {
            return results.add(head);
        }
        if (head.isReference() && head.asReference().definition.equals(definition)) {
            return results.add(head);
        }
        if (head.isGraph()) {
            return results.add(getAllRecursive(head.asGraph(), definition));
        }
        return results;
    }

    public static ImmutableList<ParseItem> getAllRoots(final ParseGraph graph, final Token definition) {
        return getAllRootsRecursive(ImmutableList.create(new Pair(checkNotNull(graph, "graph"), null)), checkNotNull(definition, "definition"), new ImmutableList<>()).computeResult();
    }

    private static SafeTrampoline<ImmutableList<ParseItem>> getAllRootsRecursive(final ImmutableList<Pair> backlog, final Token definition, final ImmutableList<ParseItem> rootList) {
        if (backlog.isEmpty()) { return complete(() -> rootList); }
        final ParseItem item = backlog.head.item;
        final ParseGraph parent = backlog.head.parent;
        final ImmutableList<ParseItem> nextResult = item.getDefinition().equals(definition) && (parent == null || !parent.getDefinition().equals(definition)) ? rootList.add(item) : rootList;
        if (item.isGraph() && !item.asGraph().isEmpty()) {
            final ParseGraph itemGraph = item.asGraph();
            return intermediate(() -> getAllRootsRecursive(backlog.tail.add(new Pair(itemGraph.head, itemGraph))
                                                                       .add(new Pair(itemGraph.tail, itemGraph)),
                                                                       definition, nextResult));
        }
        return intermediate(() -> getAllRootsRecursive(backlog.tail, definition, nextResult));
    }

    static class Pair {
        public final ParseItem item;
        public final ParseGraph parent;

        Pair(final ParseItem item, final ParseGraph parent) {
            this.item = checkNotNull(item, "item");
            this.parent = parent;
        }
    }

}
