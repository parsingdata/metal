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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.data.selection.ByType.getReferences;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;

public class EqualityTest {

    public static final Token LINKED_LIST_1 =
        seq("linkedlist",
            def("header", con(1), eq(con(0))),
            def("next", con(1)),
            opt(sub(token("linkedlist"), last(ref("next")))),
            def("footer", con(1), eq(con(1)))
        );

    public static final Token LINKED_LIST_COMPOSED_IDENTICAL =
        seq(LINKED_LIST_1,
            sub(LINKED_LIST_1, con(0)));

    @Test
    public void cycleWithIdenticalTokens() throws IOException {
        final ParseResult result = LINKED_LIST_COMPOSED_IDENTICAL.parse(stream(0, 0, 1), enc());
        assertTrue(result.succeeded);
        assertEquals(1, getAllValues(result.environment.order, "header").size);
        assertEquals(2, getReferences(result.environment.order).size);
    }

}
