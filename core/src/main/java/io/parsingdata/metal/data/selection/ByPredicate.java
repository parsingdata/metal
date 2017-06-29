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

public final class ByPredicate {

    private ByPredicate() {}

    public static final int NO_LIMIT = -1;

    public static ImmutableList<ParseValue> getAllValues(final ParseGraph graph, final Predicate<ParseValue> predicate, final int limit) {
        return getAllValues(ImmutableList.create(graph), new ImmutableList<>(), predicate, limit).computeResult();
    }

    public static ImmutableList<ParseValue> getAllValues(final ParseGraph graph, final Predicate<ParseValue> predicate) {
        return getAllValues(graph, predicate, NO_LIMIT);
    }

    private static SafeTrampoline<ImmutableList<ParseValue>> getAllValues(final ImmutableList<ParseGraph> graphList, final ImmutableList<ParseValue> valueList, final Predicate<ParseValue> predicate, final int limit) {
        if (graphList.isEmpty() || valueList.size == limit) { return complete(() -> valueList); }
        final ParseGraph graph = graphList.head;
        if (graph.isEmpty()) {
            return intermediate(() -> getAllValues(graphList.tail, valueList, predicate, limit));
        }
        return intermediate(() -> getAllValues(addIfGraph(graphList.tail.add(graph.tail), graph.head),
                                               addIfMatchingValue(valueList, graph.head, predicate),
                                               predicate,
                                               limit));
    }

    private static ImmutableList<ParseGraph> addIfGraph(final ImmutableList<ParseGraph> graphList, final ParseItem item) {
        if (item.isGraph()) { return graphList.add(item.asGraph()); }
        return graphList;
    }

    private static ImmutableList<ParseValue> addIfMatchingValue(final ImmutableList<ParseValue> valueList, final ParseItem item, final Predicate<ParseValue> predicate) {
        if (item.isValue() && predicate.test(item.asValue())) { return valueList.add(item.asValue()); }
        return valueList;
    }

}
