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

import java.util.function.Predicate;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.expression.value.Value;

public class ByPredicate {

    public static SafeTrampoline<ImmutableList<Value>> getAllValues(final ImmutableList<ParseGraph> graphList, final ImmutableList<Value> valueList, final Predicate<ParseValue> predicate) {
        if (graphList.isEmpty()) { return complete(() -> valueList); }
        final ParseGraph graph = graphList.head;
        if (graph.isEmpty()) { return intermediate(() -> getAllValues(graphList.tail, valueList, predicate)); }
        return intermediate(() -> getAllValues(addIfGraph(graphList.tail.add(graph.tail), graph.head),
            addIfMatchingValue(valueList, graph.head, predicate),
            predicate));
    }

    private static ImmutableList<ParseGraph> addIfGraph(final ImmutableList<ParseGraph> graphList, final ParseItem item) {
        if (item.isGraph()) { return graphList.add(item.asGraph()); }
        return graphList;
    }

    private static ImmutableList<Value> addIfMatchingValue(final ImmutableList<Value> valueList, final ParseItem item, final Predicate<ParseValue> predicate) {
        if (item.isValue() && predicate.test(item.asValue())) { return valueList.add(item.asValue()); }
        return valueList;
    }

}
