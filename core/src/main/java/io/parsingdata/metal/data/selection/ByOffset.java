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
import static io.parsingdata.metal.data.selection.ByToken.getAllRoots;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.token.Token;

public final class ByOffset {

    private ByOffset() {}

    public static boolean hasRootAtOffset(final ParseGraph graph, final Token definition, final long offset, final Source source) {
        return findItemAtOffset(getAllRoots(graph, definition), offset, source) != null;
    }

    public static ParseItem findItemAtOffset(final ImmutableList<ParseItem> items, final long offset, final Source source) {
        checkNotNull(items, "items");
        checkNotNull(source, "source");
        if (items.isEmpty()) { return null; }
        final ParseItem head = items.head;
        if (head.isValue() && matchesLocation(head.asValue(), offset, source)) { return head; }
        if (head.isGraph()) {
            final ParseValue value = getLowestOffsetValue(head.asGraph(), null);
            if (value != null && matchesLocation(value, offset, source)) { return head; }
        }
        return findItemAtOffset(items.tail, offset, source);
    }

    private static boolean matchesLocation(final ParseValue value, final long offset, final Source source) {
        return value.slice.offset == offset && value.slice.source == source;
    }

    private static ParseValue getLowestOffsetValue(final ParseGraph graph, final ParseValue lowest) {
        if (graph.isEmpty() || !graph.getDefinition().isLocal()) { return lowest; }
        if (graph.head.isValue()) {
            return getLowestOffsetValue(graph.tail, lowest == null || lowest.slice.offset > graph.head.asValue().slice.offset ? graph.head.asValue() : lowest);
        }
        if (graph.head.isGraph()) {
            return getLowestOffsetValue(graph.tail, getLowestOffsetValue(graph.head.asGraph(), lowest));
        }
        return getLowestOffsetValue(graph.tail, lowest);
    }

}
