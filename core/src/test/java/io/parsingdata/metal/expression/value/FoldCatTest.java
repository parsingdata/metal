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

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;

public class FoldCatTest {

    @Test
    public void foldCatRegular() {
        assertEquals("abc", foldString("any").get().asString());
    }

    @Test
    public void foldCatEmpty() {
        assertEquals(Optional.empty(), foldString("other"));
    }

    private Optional<Value> foldString(final String name) {
        final Optional<ParseState> result =
            seq(any("any"),
                any("any"),
                any("any")).parse(new Environment(stream("abc", StandardCharsets.US_ASCII), enc()));
        assertTrue(result.isPresent());
        ImmutableList<Optional<Value>> values = cat(ref(name)).eval(result.get(), enc());
        assertEquals(1, values.size);
        return values.head;
    }

    @Test
    public void foldCatEmptyResult() {
        ImmutableList<Optional<Value>> values = cat(div(con(1), con(0))).eval(EMPTY_PARSE_STATE, enc());
        assertEquals(1, values.size);
        assertEquals(Optional.empty(), values.head);
    }

}
