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

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.EMPTY_VE;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static org.junit.Assert.assertFalse;

public class DefSizeTest {
    public static final Token FORMAT =
        seq(
            def("length", con(4)),
            def("data", ref("length"))
        );

    @Test
    public void testValidLength() throws IOException {
        final ByteStream stream = new InMemoryByteStream(new byte[]{
            0x00, 0x00, 0x00, 0x02, // length = 2
            0x04, 0x08
        });
        final ParseResult result = FORMAT.parse(new Environment(stream), new Encoding());

        Assert.assertTrue(result.succeeded);
        Assert.assertArrayEquals(
            new byte[]{0x04, 0x08},
            ByName.getValue(result.environment.order, "data").getValue()
        );
    }

    @Test
    public void testInvalidLength() throws IOException {
        final ByteStream stream = new InMemoryByteStream(new byte[]{
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, // length = -1
            0x04, 0x08
        });
        final ParseResult result = FORMAT.parse(new Environment(stream), new Encoding());

        Assert.assertFalse(result.succeeded);
        // The top-level Token (Seq) has failed, so no values are recorded in the ParseGraph.
        Assert.assertEquals(ParseGraph.EMPTY, result.environment.order);
    }

    @Test
    public void testEmptyLengthInList() throws IOException {
        assertFalse(def("a", EMPTY_VE).parse(stream(1, 2, 3, 4), enc()).succeeded);
        final Token aList = seq(any("a"), any("a"));
        assertFalse(seq(aList, def("b", ref("a"))).parse(stream(1, 2, 3, 4), enc()).succeeded);
    }

}
