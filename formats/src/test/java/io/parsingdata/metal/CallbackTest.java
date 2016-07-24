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

import io.parsingdata.metal.data.OptionalValueList;
import org.junit.Test;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.format.Callback.crc32;
import static io.parsingdata.metal.format.Callback.inflate;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.junit.Assert.*;

public class CallbackTest {

    @Test
    public void crc32Good() {
        final OptionalValueList result = crc32(con(0x01020304)).eval(stream(), enc());
        assertEquals(1, result.size);
        assertTrue(result.head.isPresent());
        assertArrayEquals(new byte[] { -74, 60, -5, -51 }, result.head.get().getValue());
    }

    @Test
    public void inflateDataFormatError() {
        final OptionalValueList result = inflate(con(0xffffffff)).eval(stream(), enc());
        assertEquals(1, result.size);
        assertFalse(result.head.isPresent());
    }

}
