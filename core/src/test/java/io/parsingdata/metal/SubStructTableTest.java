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

package io.parsingdata.metal;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import org.junit.Test;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubStructTableTest {

    private final Token struct =
        seq(def("header", con(1), eq(con(42))),
            def("footer", con(1), eq(con(84))));

    private final Token table =
        seq(def("tableSize", con(1)),
            repn(def("pointer", con(1)), ref("tableSize")),
            sub(struct, ref("pointer")));

    @Test
    public void table() throws IOException {
        final Environment env = stream(3, 6, 4, 9, 42, 84, 42, 84, 0, 42, 84);
                            /* offset: 0, 1, 2, 3,  4,  5,  6,  7, 8,  9, 10
                             * count:  ^
                             * pointers:  ^, ^, ^
                             * ref1:      +----------------^^--^^
                             * ref2:         +-----^^--^^
                             * ref3:            +---------------------^^--^^
                             */
        final ParseResult res = table.parse(env, enc());
        assertTrue(res.succeeded);
        assertEquals(4, res.environment.offset);
        final ParseGraph order = res.environment.order;
        checkStruct(order.head.asGraph().head.asGraph().head.asGraph());
        checkStruct(order.head.asGraph().head.asGraph().tail.head.asGraph());
        checkStruct(order.head.asGraph().head.asGraph().tail.tail.head.asGraph());
    }

    @Test
    public void tableWithDuplicate() throws IOException {
        final Environment env = stream(4, 7, 5, 5, 10, 42, 84, 42, 84, 0, 42, 84);
                            /* offset: 0, 1, 2, 3,  4,  5,  6,  7, 8,  9, 10, 11
                             * count:  ^
                             * pointers:  ^, ^, ^, ^^
                             * ref1:      +--------------------^^--^^
                             * ref2:         +---------^^--^^
                             * ref3:         +---------^^--^^ duplicate!
                             * ref4:               ++---------------------^^--^^
                             */
        final ParseResult res = table.parse(env, enc());
        assertTrue(res.succeeded);
        assertEquals(5, res.environment.offset);
        final ParseGraph order = res.environment.order;
        checkStruct(order.head.asGraph().head.asGraph().head.asGraph());
        assertTrue(order.head.asGraph().head.asGraph().tail.head.isRef());
        checkStruct(order.head.asGraph().head.asGraph().tail.head.asRef().resolve(order));
        checkStruct(order.head.asGraph().head.asGraph().tail.tail.head.asGraph());
        checkStruct(order.head.asGraph().head.asGraph().tail.tail.tail.head.asGraph());
    }

    private void checkStruct(final ParseGraph graph) {
        assertEquals(84, graph.head.asValue().asNumeric().intValue());
        assertEquals(42, graph.tail.head.asValue().asNumeric().intValue());
    }

}
