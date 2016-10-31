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

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseItemList;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Token;

public final class ByOffset {

    private ByOffset() {}

    public static boolean hasRootAtOffset(final ParseGraph graph, final Token definition, final long offset) {
        return findItemAtOffset(getAllRoots(graph, definition), offset) != null;
    }

    public static ParseItem findItemAtOffset(final ParseItemList items, final long offset) {
        checkNotNull(items, "items");

        return items.tailStream()
            .filter(list -> {
                return !list.isEmpty();
            })
            .map(ParseItemList::getHead)
            .filter(head -> {
                if (head.isValue() && head.asValue().getOffset() == offset) {
                    return true;
                }
                else if (head.isGraph()) {
                    final ParseValue value = getLowestOffsetValue(head.asGraph(), null);
                    return value != null && value.getOffset() == offset;
                }
                return false;
            }).findFirst().orElse(null);
    }

    private static ParseValue getLowestOffsetValue(final ParseGraph graph, final ParseValue lowest) {
        if (graph.isEmpty() || !graph.getDefinition().isLocal()) { return lowest; }
        if (graph.head.isValue()) {
            return getLowestOffsetValue(graph.tail, lowest == null || lowest.getOffset() > graph.head.asValue().getOffset() ? graph.head.asValue() : lowest);
        }
        if (graph.head.isGraph()) {
            return getLowestOffsetValue(graph.tail, getLowestOffsetValue(graph.head.asGraph(), lowest));
        }
        return getLowestOffsetValue(graph.tail, lowest);
/*
checkNotNull(graph, "graph");
        if (!graph.containsValue()) { throw new IllegalStateException("Cannot determine lowest offset if graph does not contain a value."); }
        ParseGraph gr = graph;
        ParseValue low = null;
        while (gr.head != null) {
            final ParseItem head = gr.head;
            if (head.isValue()) {
                low = low != null && low.getOffset() < (head.asValue()).getOffset() ? low : head.asValue();
            }
            gr = gr.tail;
        }
        return low;*/
    }

}
