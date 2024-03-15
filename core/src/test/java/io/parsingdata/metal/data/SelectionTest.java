/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.data.Selection.findItemAtOffset;
import static io.parsingdata.metal.data.Selection.getAllValues;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class SelectionTest {

    private Source createSource() {
        return new Source() {
            @Override protected byte[] getData(BigInteger offset, BigInteger length) { return new byte[0]; }
            @Override protected boolean isAvailable(BigInteger offset, BigInteger length) { return true; }
            @Override public int immutableHashCode() { return 0; }
            @Override
            public boolean equals(Object obj) { return obj == this; }
        };
    }

    private final Source source = createSource();
    private final Source otherSource = createSource();

    @Test
    public void findItemAtOffsetTest() {
        assertEquals("the_one",
            findItemAtOffset(ImmutableList.create(ParseGraph.EMPTY.add(new ParseValue("two", any("a"), Slice.createFromSource(source, BigInteger.valueOf(2), BigInteger.valueOf(2)).get(), enc()))
                                                                  .add(new ParseValue("zero", any("a"), Slice.createFromSource(source, ZERO, BigInteger.valueOf(2)).get(), enc()))
                                                                  .add(new ParseValue("the_one", any("a"), Slice.createFromSource(source, ONE, BigInteger.valueOf(2)).get(), enc()))), ZERO, source).computeResult().get().asGraph().head.asValue().name);
        assertEquals("zero",
            findItemAtOffset(ImmutableList.<ParseItem>create(new ParseValue("zero", any("a"), Slice.createFromSource(source, ZERO, BigInteger.valueOf(2)).get(), enc()))
                                                        .addHead(new ParseValue("offsetMatchOtherSource", any("a"), Slice.createFromSource(otherSource, ZERO, BigInteger.valueOf(2)).get(), enc()))
                                                        .addHead(new ParseValue("otherOffsetMatchSource", any("a"), Slice.createFromSource(source, ONE, BigInteger.valueOf(2)).get(), enc())), ZERO, source).computeResult().get().asValue().name);
    }

    @Test
    public void limit() {
        Optional<ParseState> parseState = rep(any("a")).parse(env(stream(1, 2, 3, 4, 5)));
        assertTrue(parseState.isPresent());
        for (int i = 0; i < 7; i++) {
            ImmutableList<ParseValue> parseValues = getAllValues(parseState.get().order, (value) -> value.matches("a"), i);
            assertEquals(Math.min(5, i), (long) parseValues.size());
        }
    }

}
