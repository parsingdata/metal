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
import io.parsingdata.metal.data.ParseGraphList;
import io.parsingdata.metal.data.ParseItem;

import static io.parsingdata.metal.Util.checkNotNull;

public final class ByType {

    private ByType() {}

    public static ParseGraphList getRefs(final ParseGraph graph) {
        checkNotNull(graph, "graph");
        return getRefs(graph, graph);
    }

    private static ParseGraphList getRefs(final ParseGraph graph, final ParseGraph root) {
        if (graph.isEmpty()) { return ParseGraphList.EMPTY; }
        final ParseItem head = graph.head;
        if (head.isRef() && head.asRef().resolve(root) == null) { throw new IllegalStateException("A ref must point to an existing graph."); }
        return getRefs(graph.tail, root).add(head.isGraph() ? getRefs(head.asGraph(), root) : (head.isRef() ? ParseGraphList.EMPTY.add(head.asRef().resolve(root)) : ParseGraphList.EMPTY));
    }

    public static ParseGraphList getGraphs(final ParseGraph graph) {
        checkNotNull(graph, "graph");
        return getNestedGraphs(graph).add(graph);
    }

    private static ParseGraphList getNestedGraphs(final ParseGraph graph) {
        if (graph.isEmpty()) { return ParseGraphList.EMPTY; }
        final ParseGraphList tailGraphs = getNestedGraphs(graph.tail);
        final ParseItem head = graph.head;
        if (head.isGraph()) { return tailGraphs.add(head.asGraph()).add(getNestedGraphs(head.asGraph())); }
        return tailGraphs;
    }

}
