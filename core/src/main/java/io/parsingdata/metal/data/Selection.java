/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal.data;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.token.Token;

public final class Selection {

    public static final int NO_LIMIT = -1;

    private Selection() {}

    public static Trampoline<Optional<ParseItem>> findItemAtOffset(final ImmutableList<ParseItem> items, final BigInteger offset, final Source source) {
        checkNotNull(items, "items");
        checkNotNull(source, "source");
        if (items.isEmpty()) {
            return complete(Optional::empty);
        }
        final ParseItem head = items.head();
        if (head.isValue() && matchesLocation(head.asValue(), offset, source)) {
            return complete(() -> Optional.of(head));
        }
        if (head.isGraph()) {
            final ParseValue value = getLowestOffsetValue(ImmutableList.create(head.asGraph()), null).computeResult();
            if (value != null && matchesLocation(value, offset, source)) {
                return complete(() -> Optional.of(head));
            }
        }
        return intermediate(() -> findItemAtOffset(items.tail(), offset, source));
    }

    private static boolean matchesLocation(final ParseValue value, final BigInteger offset, final Source source) {
        return value.slice().offset.compareTo(offset) == 0 && value.slice().source.equals(source);
    }

    private static Trampoline<ParseValue> getLowestOffsetValue(final ImmutableList<ParseGraph> graphList, final ParseValue lowest) {
        if (graphList.isEmpty()) {
            return complete(() -> lowest);
        }
        final ParseGraph graph = graphList.head();
        if (graph.isEmpty() || !graph.getDefinition().isLocal()) {
            return intermediate(() -> getLowestOffsetValue(graphList.tail(), lowest));
        }
        return intermediate(() -> getLowestOffsetValue(addIfGraph(graphList.tail().addHead(graph.tail), graph.head),
                                                       compareIfValue(lowest, graph.head)));
    }

    private static ParseValue compareIfValue(final ParseValue lowest, final ParseItem head) {
        return head.isValue() ? getLowest(lowest, head.asValue()) : lowest;
    }

    private static ParseValue getLowest(final ParseValue lowest, final ParseValue value) {
        return lowest == null || lowest.slice().offset.compareTo(value.slice().offset) > 0 ? value : lowest;
    }

    private static ImmutableList<ParseGraph> addIfGraph(final ImmutableList<ParseGraph> graphList, final ParseItem head) {
        return head.isGraph() ? graphList.addHead(head.asGraph()) : graphList;
    }

    public static ImmutableList<ParseValue> getAllValues(final ParseGraph graph, final Predicate<ParseValue> predicate, final int limit) {
        return getAllValues(ImmutableList.create(graph), new ImmutableList<>(), predicate, limit).computeResult();
    }

    public static ImmutableList<ParseValue> getAllValues(final ParseGraph graph, final Predicate<ParseValue> predicate) {
        return getAllValues(graph, predicate, NO_LIMIT);
    }

    public static ImmutableList<ParseValue> getAllValues(final ParseGraph graph, final Predicate<ParseValue> predicate, final int limit, final int requestedScope, final int currentScope) {
        return getAllScopedValues(graph, predicate, limit, requestedScope, currentScope).computeResult();
    }

    private static Trampoline<ImmutableList<ParseValue>> getAllScopedValues(final ParseGraph graph, final Predicate<ParseValue> predicate, final int limit, final int requestedScope, final int currentScope) {
        if (graph.isEmpty() || limit == 0) {
            return complete(ImmutableList::new);
        }
        if (requestedScope >= currentScope) {
            return complete(() -> getAllValues(graph, predicate, limit));
        }
        return intermediate(() -> getAllScopedValues(graph.head.asGraph(), predicate, limit, requestedScope, graph.head.getDefinition().isScopeDelimiter() ? currentScope - 1 : currentScope));
    }

    private static Trampoline<ImmutableList<ParseValue>> getAllValues(final ImmutableList<ParseGraph> graphList, final ImmutableList<ParseValue> valueList, final Predicate<ParseValue> predicate, final int limit) {
        if (graphList.isEmpty() || (long) valueList.size() == limit) {
            return complete(() -> valueList);
        }
        final ParseGraph graph = graphList.head();
        if (graph.isEmpty()) {
            return intermediate(() -> getAllValues(graphList.tail(), valueList, predicate, limit));
        }
        return intermediate(() -> getAllValues(addIfGraph(graphList.tail().addHead(graph.tail), graph.head),
                                               addIfMatchingValue(valueList, graph.head, predicate),
                                               predicate,
                                               limit));
    }

    private static ImmutableList<ParseValue> addIfMatchingValue(final ImmutableList<ParseValue> valueList, final ParseItem item, final Predicate<ParseValue> predicate) {
        if (item.isValue() && predicate.test(item.asValue())) {
            return valueList.addHead(item.asValue());
        }
        return valueList;
    }

    /**
     * Reverses an immutable list without modifying it.
     * @param list the list to reverse
     * @return the reverted list
     * @param <T> the type of the values in the list
     * @deprecated Use {@link ImmutableList#reverse()} instead.
     */
    @Deprecated(since = "11.0.0")
    public static <T> ImmutableList<T> reverse(final ImmutableList<T> list) {
        return list.reverse();
    }

    public static ImmutableList<ParseItem> getAllRoots(final ParseGraph graph, final Token definition) {
        return getAllRootsRecursive(ImmutableList.create(new Pair(checkNotNull(graph, "graph"), null)), checkNotNull(definition, "definition"), new ImmutableList<>()).computeResult();
    }

    private static Trampoline<ImmutableList<ParseItem>> getAllRootsRecursive(final ImmutableList<Pair> backlog, final Token definition, final ImmutableList<ParseItem> rootList) {
        if (backlog.isEmpty()) {
            return complete(() -> rootList);
        }
        final ParseItem item = backlog.head().item;
        final ParseGraph parent = backlog.head().parent;
        final ImmutableList<ParseItem> nextResult = item.getDefinition().equals(definition) && (parent == null || !parent.getDefinition().equals(definition)) ? rootList.addHead(item) : rootList;
        if (item.isGraph() && !item.asGraph().isEmpty()) {
            final ParseGraph itemGraph = item.asGraph();
            return intermediate(() -> getAllRootsRecursive(backlog.tail().addHead(new Pair(itemGraph.head, itemGraph))
                                                                       .addHead(new Pair(itemGraph.tail, itemGraph)),
                                                           definition,
                                                           nextResult));
        }
        return intermediate(() -> getAllRootsRecursive(backlog.tail(), definition, nextResult));
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
