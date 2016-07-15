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

import io.parsingdata.metal.data.ParseResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodTest {

    private static final Token NOD = nod(con(4));
    private static final Token NOD_REF_SIZE = nod(ref("size"));
    private static final Token FOUND_REF = seq(def("size", con(1)), NOD_REF_SIZE);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nodSkipsData() throws IOException {
        ParseResult parseResult = NOD.parse(stream(1, 1, 1, 1), enc());
        assertTrue(parseResult.succeeded());
        assertThat(parseResult.getEnvironment().offset, is(4L));
    }

    @Test
    public void nodWithRefSize() throws IOException {
        ParseResult parseResult = FOUND_REF.parse(stream(1, 1), enc());
        // 1 byte size, 1 byte nod:
        assertTrue(parseResult.succeeded());
        assertThat(parseResult.getEnvironment().offset, is(2L));
    }

    @Test
    public void nodWithoutSize() throws IOException {
        ParseResult parseResult = NOD_REF_SIZE.parse(stream(), enc());
        assertFalse(parseResult.succeeded());
    }

    @Test
    public void nodWithMultipleSizes() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Size may not evaluate to more than a single value.");

        seq(def("size", con(1)),
            def("size", con(1)),
            NOD_REF_SIZE).parse(stream(2, 2, 0, 0), enc());
    }

    @Test
    public void nodWithNegativeSizes() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Size must be larger than 0, but is: -1");

        seq(def("size", con(1)),
            NOD_REF_SIZE).parse(stream(-1), signed());
    }

}
