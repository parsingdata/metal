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

import static java.nio.charset.StandardCharsets.US_ASCII;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.EMPTY;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.until;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.expression.value.ValueExpression;

public class UntilTest {

    private static final String INPUT_1 = "Hello, World!";
    private static final String INPUT_2 = "Another line...";
    private static final String INPUT_3 = "Another way to scroll...";
    private static final String INPUT = INPUT_1 + "\n" + INPUT_2 + "\n" + INPUT_3 + "\n";

    @Test
    public void threeNewLines() throws IOException {
        final Optional<ParseState> parseState = createToken(con(0), post(def("newline", con(1)), eq(con('\n')))).parse(env(stream(INPUT, US_ASCII)));
        assertTrue(parseState.isPresent());
        ImmutableList<ParseValue> values = getAllValues(parseState.get().order, "line");
        assertEquals(3, values.size);
        assertEquals(values.head.asString(), INPUT_3);
        assertEquals(values.tail.head.asString(), INPUT_2);
        assertEquals(values.tail.tail.head.asString(), INPUT_1);
    }

    @Test
    public void untilInclusive() throws IOException {
        final Optional<ParseState> parseState = createToken(con(1), post(EMPTY, eq(mod(last(ref("line")), con(256)), con('\n')))).parse(env(stream(INPUT, US_ASCII)));
        assertTrue(parseState.isPresent());
        ImmutableList<ParseValue> values = getAllValues(parseState.get().order, "line");
        assertEquals(3, values.size);
        assertEquals(values.head.asString(), INPUT_3 + '\n');
        assertEquals(values.tail.head.asString(), INPUT_2 + '\n');
        assertEquals(values.tail.tail.head.asString(), INPUT_1 + '\n');
    }

    @Test
    public void allDefaultValueExpressions() throws IOException {
        assertTrue(until("value", def("terminator", 1, eq(con(0)))).parse(env(stream(1, 2, 3, 0))).isPresent());
    }

    @Test
    public void errorNegativeSize() {
        assertFalse(until("value", con(-1, signed()), def("terminator", 1, eq(con(0)))).parse(env(stream(1, 2, 3, 0))).isPresent());
    }

    private Token createToken(final ValueExpression initialSize, final Token terminator) {
        return repn(until("line", initialSize, terminator), con(3));
    }

}
