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

package io.parsingdata.metal.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.CAT_REDUCER;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.rev;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ParseResult;

public class TieTest {

    private static final Token increasing =
        seq(def("one", 1, eq(con(1))),
            def("two", 1, eq(con(2))),
            def("three", 1, eq(con(3))),
            def("four", 1, eq(con(4))),
            def("five", 1, eq(con(5))),
            def("six", 1, eq(con(6))));

    private static final Token container =
        seq(def("blockSize", 1),
            def("tableSize", 1),
            repn(any("offset"), last(ref("tableSize"))),
            sub(def("data", last(ref("blockSize"))), ref("offset")),
            tie(increasing, fold(rev(ref("data")), CAT_REDUCER)));

    @Test
    public void containerWithIncreasing() throws IOException {
        final ParseResult result = container.parse(stream(2, 3, 7, 5, 9, 3, 4, 1, 2, 5, 6), enc());
        assertTrue(result.succeeded);
        assertEquals(5, result.environment.offset);
    }

}
