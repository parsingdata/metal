/**
 * Copyright 2016 National Cyber Security Centre, Netherlands Forensic Institute
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
package io.parsingdata.metal.util;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseRef;
import io.parsingdata.metal.data.ParseValue;

/**
 * Utility class used to update a value in a certain environment or graph.
 *
 * @author Netherlands Forensic Institute.
 */
public final class ValueUpdater {

    private ValueUpdater() {
    }

    public static Environment updateEnv(final Environment originalEnvironment, final ParseValue newValue) {
        return new Environment(updateGraph(newValue, originalEnvironment.order), originalEnvironment.input, originalEnvironment.offset);
    }

    /**
     * Updates a {@link ParseValue} in a {@link ParseGraph}, effectively replacing the value
     * at the offset of the given value, with this value.
     *
     * @param newValue the new value to insert into the graph
     * @param graph the graph to be updated
     * @return a graph containing the new value, or an equal graph when there is no value present at the offset of the new value
     */
    public static ParseGraph updateGraph(final ParseValue newValue, final ParseGraph graph) {
        return updateGraph(newValue, graph, ParseGraph.EMPTY).reverse();
    }

    private static ParseGraph updateGraph(final ParseValue newValue, final ParseGraph graph, final ParseGraph returnGraph) {
        ParseGraph newGraph = returnGraph;
        final ParseItem head = graph.head;
        if (head == null) {
            return newGraph;
        }
        if (head.isGraph()) {
            newGraph = updateGraph(newValue, head.asGraph(), newGraph.addBranch(head.getDefinition())).closeBranch();
        }
        else if (head.isValue()) {
            newGraph = newGraph.add(head.asValue().getOffset() == newValue.getOffset() ? newValue : head.asValue());
        }
        else if (head.isRef()) {
            newGraph = newGraph.add(new ParseRef(head.asRef().location, head.getDefinition()));
        }
        return updateGraph(newValue, graph.tail, newGraph);
    }
}
