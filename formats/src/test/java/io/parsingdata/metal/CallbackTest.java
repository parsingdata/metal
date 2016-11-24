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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.format.Callback.crc32;
import static io.parsingdata.metal.format.Callback.inflate;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.expression.value.OptionalValue;

public class CallbackTest {

    @Test
    public void crc32Good() throws IOException {
        final ImmutableList<OptionalValue> result = crc32(con(0x01020304)).eval(stream(), enc());
        assertEquals(1, result.size);
        assertTrue(result.head.isPresent());
        assertArrayEquals(new byte[] { -74, 60, -5, -51 }, result.head.get().getValue());
    }

    @Test
    public void inflateGood() throws IOException {
        final ImmutableList<OptionalValue> result = inflate(con(0xcb, 0x4d, 0x2d, 0x49, 0xcc, 0x01, 0x00)).eval(stream(), enc());
        assertEquals(1, result.size);
        assertTrue(result.head.isPresent());
        assertEquals("metal", result.head.get().asString());
    }

    @Test
    public void inflateDataFormatError() throws IOException {
        final ImmutableList<OptionalValue> result = inflate(con(0xffffffff)).eval(stream(), enc());
        assertEquals(1, result.size);
        assertFalse(result.head.isPresent());
    }

}
