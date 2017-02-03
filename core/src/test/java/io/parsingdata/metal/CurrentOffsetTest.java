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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.currentOffset;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.self;
import static io.parsingdata.metal.Shorthand.sub;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.reference.CurrentOffset;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class CurrentOffsetTest {

    private static final Encoding ENCODING = new Encoding();
    private static final ByteStream NO_BYTES = new InMemoryByteStream(new byte[0]);

    private static final CurrentOffset CURRENT_OFFSET = new CurrentOffset();

    @Test
    public void currentOffset() {
        final Environment environment = new Environment(NO_BYTES, 42);

        final ImmutableList<Optional<Value>> currentOffset = CURRENT_OFFSET.eval(environment.order, ENCODING);

        assertNotNull(currentOffset);
        assertEquals(1, currentOffset.size);
        assertEquals(42, currentOffset.head.get().asNumeric().longValue());
    }

    @Test
    public void currentOffsetLarger() {
        // offset would flip signed bit if interpreted as signed integer:
        final Environment environment = new Environment(NO_BYTES, 128);

        final ImmutableList<Optional<Value>> currentOffset = CURRENT_OFFSET.eval(environment.order, ENCODING);

        assertNotNull(currentOffset);
        assertEquals(1, currentOffset.size);
        assertEquals(128, currentOffset.head.get().asNumeric().longValue());
    }

    @Test
    public void currentOffsetInCalculations() throws IOException {
        final byte[] stream = new byte[256];
        for (int i = 0; i < stream.length; i++) {
            stream[i] = (byte) i;
        }
        final Environment environment = new Environment(new InMemoryByteStream(stream));

        // value - offset + 1 should be 0:
        final Token offsetValidation = rep(def("byte", con(1), eqNum(sub(self, sub(currentOffset, con(1))), con(0))));

        final ParseResult result = offsetValidation.parse(environment, new Encoding(Sign.UNSIGNED));
        assertTrue(result.succeeded);
        assertEquals(256, result.environment.offset);
    }

}
