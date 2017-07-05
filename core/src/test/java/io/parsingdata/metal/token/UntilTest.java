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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.until;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.util.InMemoryByteStream;

public class UntilTest {

    @Test
    public void threeNewLines() throws IOException {
        final String input1 = "Hello, World!";
        final String input2 = "Another line...";
        final String input3 = "Another way to scroll...";
        final String input = input1 + "\n" + input2 + "\n" + input3 + "\n";
        final Optional<Environment> environment =
            repn(
                until(
                    "line",
                    post(def("newline", con(1)), eq(con('\n')))),
                con(3)
            ).parse(new Environment(new InMemoryByteStream(input.getBytes(StandardCharsets.US_ASCII))), enc());
        assertTrue(environment.isPresent());
        ImmutableList<ParseValue> values = getAllValues(environment.get().order, "line");
        assertEquals(3, values.size);
        assertEquals(values.head.asString(), input3);
        assertEquals(values.tail.head.asString(), input2);
        assertEquals(values.tail.tail.head.asString(), input1);
    }

}
