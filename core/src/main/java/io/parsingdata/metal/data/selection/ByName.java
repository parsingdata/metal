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
import static io.parsingdata.metal.data.ParseValueList.EMPTY;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueList;

public final class ByName {

    private ByName() {}

    /**
     * @param graph The graph to search
     * @param name Name of the value
     * @return The first value (bottom-up) with the provided name in the provided graph
     */
    public static ParseValue getValue(final ParseGraph graph, final String name) {
        checkNotNull(graph, "graph");
        checkNotNull(name, "name");
        if (graph.isEmpty()) { return null; }
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().matches(name)) {
            return head.asValue();
        }
        if (head.isGraph()) {
            final ParseValue value = getValue(head.asGraph(), name);
            if (value != null) { return value; }
        }
        return getValue(graph.tail, name);
    }

    /**
     * @param graph The graph to search
     * @param name Name of the value
     * @return All values with the provided name in this graph
     */
    public static ParseValueList getAllValues(final ParseGraph graph, final String name) {
        checkNotNull(graph, "graph");
        checkNotNull(name, "name");
        return getAllValuesRecursive(graph, name);
    }

    private static ParseValueList getAllValuesRecursive(final ParseGraph graph, final String name) {
        if (graph.isEmpty()) { return EMPTY; }
        final ParseValueList tailResults = getAllValuesRecursive(graph.tail, name);
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().matches(name)) {
            return tailResults.add(head.asValue());
        }
        if (head.isGraph()) {
            return tailResults.add(getAllValuesRecursive(head.asGraph(), name));
        }
        return tailResults;
    }

}
