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
import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.elvis;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.rev;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.util.InMemoryByteStream;

public class TieTest {

    // Starts at 1, then increases with 1, modulo 100.
    private static final Token INC_PREV_MOD_100 =
        rep(def("value", 1, eq(mod(add(elvis(nth(rev(ref("value")), con(1)), con(0)), con(1)), con(100)))));

    private static final Token CONTAINER =
        seq(def("blockSize", 1),
            def("tableSize", 1),
            repn(any("offset"), last(ref("tableSize"))),
            sub(def("data", last(ref("blockSize"))), ref("offset")),
            tie(INC_PREV_MOD_100, fold(rev(ref("data")), CAT_REDUCER)));

    @Test
    public void smallContainer() throws IOException {
        final ParseResult result = parseContainer();
        assertEquals(5, result.environment.offset);
        assertEquals(6, getAllValues(result.environment.order, "value").size);
    }

    @Test
    public void checkContainerSource() throws IOException {
        final ParseResult result = parseContainer();
        ImmutableList<ParseValue> values = getAllValues(result.environment.order, "value");
        final Slice slice = values.head.slice;
        parseIncreasing(slice.getData());
    }

    private ParseResult parseContainer() throws IOException {
        final ParseResult result = CONTAINER.parse(stream(2, 3, 7, 5, 9, 3, 4, 1, 2, 5, 6), enc());
        assertTrue(result.succeeded);
        return result;
    }

    @Test
    public void increasing() throws IOException {
        final byte[] data = new byte[1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)((i+1) % 100);
        }
        assertEquals(1024, parseIncreasing(data).environment.offset);
    }

    private ParseResult parseIncreasing(final byte[] data) throws IOException {
        final ParseResult result = INC_PREV_MOD_100.parse(new Environment(new InMemoryByteStream(data)), enc());
        assertTrue(result.succeeded);
        return result;
    }

}
