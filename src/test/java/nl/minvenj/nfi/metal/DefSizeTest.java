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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.ref;
import static nl.minvenj.nfi.metal.Shorthand.seq;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.minvenj.nfi.metal.data.ByteStream;
import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.token.Token;
import nl.minvenj.nfi.metal.util.InMemoryByteStream;

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

        Assert.assertTrue(result.succeeded());
        Assert.assertArrayEquals(
            new byte[]{0x04, 0x08},
            result.getEnvironment().order.flatten().get("data").getValue()
        );
    }

    @Test
    public void testInvalidLength() throws IOException {
        final ByteStream stream = new InMemoryByteStream(new byte[]{
            -1, -1, -1, -1, // length = -1
            0x04, 0x08
        });
        final ParseResult result = FORMAT.parse(new Environment(stream), new Encoding());

        Assert.assertFalse(result.succeeded());
        Assert.assertEquals(-1, result.getEnvironment().order.flatten().get("length").asNumeric().intValue());
    }
}
