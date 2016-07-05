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
import io.parsingdata.metal.data.ParseValue;

public class ByOffset {

    public static boolean hasGraphAtRef(final ParseGraph graph, final long ref) {
        return findRef(ByType.getGraphs(graph), ref) != null;
    }

    public static ParseGraph findRef(final ParseGraphList graphs, final long ref) {
        ParseGraphList gr = graphs;
        ParseGraph best = null;

        while (gr.head != null) {
            if (gr.head.containsValue() && gr.head.getLowestOffsetValue().getOffset() == ref) {
                best = gr.head;
            }
            gr = gr.tail;
        }
        return best;
    }

    public static ParseValue getLowestOffsetValue(final ParseGraph graph) {
        if (!graph.containsValue()) {
            throw new IllegalStateException("Cannot determine lowest offset if graph does not contain a value.");
        }

        ParseGraph gr = graph;
        ParseValue low = null;
        while (gr.head != null) {
            final ParseItem head = gr.head;
            if (head.isValue()) {
                low = low != null && low.getOffset() < (head.asValue()).getOffset() ? low : head.asValue();
            }
            gr = gr.tail;
        }

        return low;
    }
}
