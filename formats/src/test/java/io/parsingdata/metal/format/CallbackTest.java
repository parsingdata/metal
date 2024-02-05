/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

package io.parsingdata.metal.format;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Util.inflate;
import static io.parsingdata.metal.format.Callback.crc32;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.expression.value.Value;

public class CallbackTest {

    @Test
    public void crc32Good() {
        final ImmutableList<Value> result = crc32(con(0x01020304)).eval(stream(), enc());
        assertEquals(1, result.size());
        assertArrayEquals(new byte[] { -74, 60, -5, -51 }, result.head().value());
    }

    @Test
    public void inflateGood() {
        final ImmutableList<Value> result = inflate(con(0xcb, 0x4d, 0x2d, 0x49, 0xcc, 0x01, 0x00)).eval(stream(), enc());
        assertEquals(1, result.size());
        assertEquals("metal", result.head().asString());
    }

}
