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
import static io.parsingdata.metal.data.transformation.Reversal.reverse;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.expression.value.Value;

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
    public static ImmutableList<Value> getAllValues(final ParseGraph graph, final String name) {
        checkNotNull(graph, "graph");
        checkNotNull(name, "name");
        return reverse(getAllValuesRecursive(ImmutableList.create(graph), new ImmutableList<>(), name).computeResult());
    }

    private static SafeTrampoline<ImmutableList<Value>> getAllValuesRecursive(final ImmutableList<ParseGraph> graphList, final ImmutableList<Value> values, final String name) {
        if (graphList.isEmpty()) { return complete(() -> values); }
        final ParseGraph graph = graphList.head;
        if (graph.isEmpty()) { return intermediate(() -> getAllValuesRecursive(graphList.tail, values, name)); }
        final ParseItem head = graph.head;
        if (head.isValue() && head.asValue().matches(name)) {
            return intermediate(() -> getAllValuesRecursive(graphList.tail.add(graph.tail), values.add(head.asValue()), name));
        }
        if (head.isGraph()) {
            return intermediate(() -> getAllValuesRecursive(graphList.tail.add(graph.tail).add(graph.head.asGraph()), values, name));
        }
        return intermediate(() -> getAllValuesRecursive(graphList.tail.add(graph.tail), values, name));
    }

    public static ParseValue get(final ImmutableList<ParseValue> list, final String name) {
        if (list.isEmpty()) { return null; }
        if (list.head.matches(name)) { return list.head; }
        else {
            return get(list.tail, name);
        }
    }

    public static ImmutableList<ParseValue> getAll(final ImmutableList<ParseValue> list, final String name) {
        if (list.isEmpty()) { return list; }
        final ImmutableList<ParseValue> tailList = getAll(list.tail, name);
        if (list.head.matches(name)) {
            return tailList.add(list.head);
        } else { return tailList; }
    }

}
