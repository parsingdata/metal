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

package io.parsingdata.metal.data.transformation;

import static io.parsingdata.metal.SafeTrampoline.complete;
import static io.parsingdata.metal.SafeTrampoline.intermediate;

import io.parsingdata.metal.SafeTrampoline;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;

public final class Reversal {

    private Reversal() {}

    public static ParseGraph reverse(final ParseGraph graph) {
        return reverse(graph, ParseGraph.EMPTY);
    }

    private static ParseGraph reverse(final ParseGraph oldGraph, final ParseGraph newGraph) {
        if (oldGraph.isEmpty()) { return newGraph; }
        return reverse(oldGraph.tail, new ParseGraph(reverseItem(oldGraph.head), newGraph, oldGraph.definition));
    }

    private static ParseItem reverseItem(final ParseItem item) {
        return item.isGraph() ? Reversal.reverse(item.asGraph(), ParseGraph.EMPTY) : item;
    }

    public static <T> ImmutableList<T> reverse(final ImmutableList<T> list) {
        if (list.isEmpty()) { return list; }
        return reverse(list.tail, ImmutableList.create(list.head)).computeResult();
    }

    private static <T> SafeTrampoline<ImmutableList<T>> reverse(final ImmutableList<T> oldList, final ImmutableList<T> newList) {
        if (oldList.isEmpty()) { return complete(() -> newList); }
        return intermediate(() -> reverse(oldList.tail, newList.add(oldList.head)));
    }

}
