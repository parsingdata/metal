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

package io.parsingdata.metal.expression.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.bytes;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.util.InMemoryByteStream;

public class BytesTest {

    public static final ParseState EMPTY_PARSE_STATE = createFromByteStream(new InMemoryByteStream(new byte[] {}));

    @Test
    public void bytesEmpty() {
        assertEquals(0, bytes(ref("random")).eval(EMPTY_PARSE_STATE, enc()).size);
    }

    @Test
    public void bytesListContainsOnlyEmpty() {
        assertEquals(0, bytes(div(con(1), con(0))).eval(EMPTY_PARSE_STATE, enc()).size);
    }

    @Test
    public void bytesListContainsEmpty() throws IOException {
        Optional<ParseState> result =
            seq(def("value", con(2)),
                def("value", con(2)),
                def("value", con(2)),
                def("divider", con(1)),
                def("divider", con(1)),
                def("divider", con(1))).parse(env(stream(1, 0, 127, 127, 127, 0, 255, 0, 1)));
        assertTrue(result.isPresent());
        final ImmutableList<Optional<Value>> bytesAfterDivision = bytes(div(ref("value"), ref("divider"))).eval(result.get(), enc());
        assertEquals(3, bytesAfterDivision.size); // 1 of the first division, 0 of the second, 2 of the third
        assertEquals(1, bytesAfterDivision.head.get().asNumeric().intValueExact()); // first value (0x0100) / first divider (0xFF)
        // second division result is missing because of division by zero
        assertEquals(0, bytesAfterDivision.tail.head.get().asNumeric().intValueExact()); // third value (0x7F00) / third divider (0x01), right byte
        assertEquals(127, bytesAfterDivision.tail.tail.head.get().asNumeric().intValueExact()); // left byte
    }

}
