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
import static io.parsingdata.metal.data.selection.ByToken.getAllRoots;

import java.util.Optional;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.token.Token;

public final class ByOffset {

    private ByOffset() {}

    public static boolean hasRootAtOffset(final ParseGraph graph, final Token definition, final long offset, final Source source) {
        return findItemAtOffset(getAllRoots(graph, definition), offset, source).computeResult().isPresent();
    }

    public static SafeTrampoline<Optional<ParseItem>> findItemAtOffset(final ImmutableList<ParseItem> items, final long offset, final Source source) {
        checkNotNull(items, "items");
        checkNotNull(source, "source");
        if (items.isEmpty()) { return complete(Optional::empty); }
        final ParseItem head = items.head;
        if (head.isValue() && matchesLocation(head.asValue(), offset, source)) { return complete(() -> Optional.of(head)); }
        if (head.isGraph()) {
            final ParseValue value = getLowestOffsetValue(ImmutableList.create(head.asGraph()), null).computeResult();
            if (value != null && matchesLocation(value, offset, source)) { return complete(() -> Optional.of(head)); }
        }
        return intermediate(() -> findItemAtOffset(items.tail, offset, source));
    }

    private static boolean matchesLocation(final ParseValue value, final long offset, final Source source) {
        return value.slice.offset == offset && value.slice.source.equals(source);
    }

    private static SafeTrampoline<ParseValue> getLowestOffsetValue(final ImmutableList<ParseGraph> graphList, final ParseValue lowest) {
        if (graphList.isEmpty()) { return complete(() -> lowest); }
        final ParseGraph graph = graphList.head;
        if (graph.isEmpty() || !graph.getDefinition().isLocal()) {
            return intermediate(() -> getLowestOffsetValue(graphList.tail, lowest));
        }
        return intermediate(() -> getLowestOffsetValue(addIfGraph(graphList.tail.add(graph.tail), graph.head),
                                                       compareIfValue(lowest, graph.head)));
    }

    private static ParseValue compareIfValue(final ParseValue lowest, final ParseItem head) {
        return head.isValue() ? getLowest(lowest, head.asValue()) : lowest;
    }

    private static ImmutableList<ParseGraph> addIfGraph(final ImmutableList<ParseGraph> graphList, final ParseItem head) {
        return head.isGraph() ? graphList.add(head.asGraph()) : graphList;
    }

    private static ParseValue getLowest(final ParseValue lowest, final ParseValue value) {
        return lowest == null || lowest.slice.offset > value.slice.offset ? value : lowest;
    }

}
