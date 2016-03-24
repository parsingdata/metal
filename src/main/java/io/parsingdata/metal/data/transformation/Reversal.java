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

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;

public class Reversal {

    public static ParseGraph reverse(ParseGraph oldGraph, ParseGraph newGraph) {
        if (oldGraph.isEmpty()) { return newGraph; }
        return reverse(oldGraph.tail, new ParseGraph(reverseItem(oldGraph.head), newGraph, oldGraph.definition));
    }

    private static ParseItem reverseItem(final ParseItem item) {
        return item.isGraph() ? item.asGraph().reverse() : item;
    }

}
