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

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseGraphList;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Token;

public final class ByOffset {

    private ByOffset() {}

    public static boolean hasGraphAtRef(final ParseGraph graph, final Token definition, final long ref) {
        return findRef(ByToken.getAllRootGraphs(graph, definition), ref) != null;
    }

    public static ParseGraph findRef(final ParseGraphList graphs, final long ref) {
        checkNotNull(graphs, "graphs");
        if (graphs.isEmpty()) { return null; }
        if (graphs.head.containsValue() && ByOffset.getLowestOffsetValue(graphs.head).getOffset() == ref) {
            return graphs.head;
        }
        return findRef(graphs.tail, ref);
    }

    public static ParseValue getLowestOffsetValue(final ParseGraph graph) {
        checkNotNull(graph, "graph");
        if (!graph.containsValue()) {
            throw new IllegalStateException("Cannot determine lowest offset if graph does not contain a value.");
        }
        return getLowestOffsetValueRecursive(graph);
    }

    private static ParseValue getLowestOffsetValueRecursive(final ParseGraph graph) {
        final ParseItem head = graph.head;
        if (head.isValue()) {
            return getLowestOffsetValue(graph.tail, head.asValue());
        }
        if (head.isGraph() && head.asGraph().containsValue()) {
            return getLowestOffsetValue(graph.tail, getLowestOffsetValueRecursive(head.asGraph()));
        }
        return getLowestOffsetValueRecursive(graph.tail);
    }

    private static ParseValue getLowestOffsetValue(final ParseGraph graph, final ParseValue lowest) {
        if (!graph.containsValue()) { return lowest; }
        final ParseItem head = graph.head;
        if (head.isValue()) {
            return getLowestOffsetValue(graph.tail, lowest.getOffset() < head.asValue().getOffset() ? lowest : head.asValue());
        }
        if (head.isGraph()) {
            return getLowestOffsetValue(graph.tail, getLowestOffsetValue(head.asGraph(), lowest));
        }
        return getLowestOffsetValue(graph.tail, lowest);
    }

}
