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

package nl.minvenj.nfi.metal.data.selection;

import nl.minvenj.nfi.metal.data.ParseGraph;
import nl.minvenj.nfi.metal.data.ParseGraphList;
import nl.minvenj.nfi.metal.data.ParseItem;
import nl.minvenj.nfi.metal.data.ParseValue;

public class ByOffset {

    public static boolean hasGraphAtRef(ParseGraph graph, long ref) {
        return findRef(ByType.getGraphs(graph), ref) != null;
    }

    public static ParseGraph findRef(final ParseGraphList graphs, final long ref) {
        if (graphs.isEmpty()) { return null; }
        final ParseGraph res = findRef(graphs.tail, ref);
        if (res != null) { return res; }
        if (graphs.head.containsValue() && graphs.head.getLowestOffsetValue().getOffset() == ref) {
            return graphs.head;
        }
        return null;
    }

    public static ParseValue getLowestOffsetValue(ParseGraph graph) {
        if (!graph.containsValue()) { throw new IllegalStateException("Cannot determine lowest offset if graph does not contain a value."); }
        final ParseItem head = graph.head;
        if (head.isValue()) {
            return getLowestOffsetValue(graph.tail, head.asValue());
        }
        return getLowestOffsetValue(graph.tail);
    }

    private static ParseValue getLowestOffsetValue(ParseGraph graph, final ParseValue lowest) {
        if (!graph.containsValue()) { return lowest; }
        final ParseItem head = graph.head;
        if (head.isValue()) {
            return getLowestOffsetValue(graph.tail, lowest.getOffset() < (head.asValue()).getOffset() ? lowest : head.asValue());
        }
        return getLowestOffsetValue(graph.tail, lowest);
    }

}
