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
import static io.parsingdata.metal.data.ParseGraph.EMPTY;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;

public final class ByItem {

    private ByItem() {}
    
    /**
     * @param graph The graph to search
     * @param lastHead The first item (bottom-up) to be excluded
     * @return The partial graph of the provided graph starting past (bottom-up) the provided lastHead
     */
    public static ParseGraph getGraphAfter(final ParseGraph graph, final ParseItem lastHead) {
        checkNotNull(graph, "graph");
        return getGraphAfterRecursive(graph, lastHead);
    }

    private static ParseGraph getGraphAfterRecursive(final ParseGraph graph, final ParseItem lastHead) {
        if (graph.isEmpty()) { return EMPTY; }
        final ParseItem head = graph.head;
        if (head == lastHead) { return EMPTY; }
        // TODO: How can we do this without calling the (previously private) constructor? (#64)
        return new ParseGraph(head, getGraphAfter(graph.tail, lastHead), graph.definition);
    }

}
