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

package nl.minvenj.nfi.ddrx;

import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.str;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.data.ValueList;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.token.StructSink;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StructSinksTest {

    @Test
    public void structSinkSingle() throws IOException {
        createToken(0L).parse(stream(1, 2), enc());
    }

    @Test
    public void structSinkMultiInRep() throws IOException {
        rep(createToken(0L, 2L)).parse(stream(1, 2, 3, 4), enc());
    }

    @Test
    public void structSinkMultiOverRep() throws IOException {
        str("outer", rep(createToken(0L, 2L)), new StructSink() {
            @Override
            public void handleStruct(String scopeName, Environment env, Encoding enc, ValueList struct) {
                Assert.assertEquals(0L, struct.tail.tail.tail.head.getOffset());
                Assert.assertNotNull(struct.head);
                Assert.assertNotNull(struct.tail.head);
                Assert.assertNotNull(struct.tail.tail.head);
                Assert.assertNotNull(struct.tail.tail.tail.head);
                Assert.assertNull(struct.tail.tail.tail.tail);
                ValueList cur = struct;
                for (int i = 0; i < 4; i++) {
                    Assert.assertEquals((i & 1) == 0 ? "b" : "a", cur.head.getName());
                    Assert.assertEquals(4-i, cur.head.asNumeric().intValue());
                    cur = cur.tail;
                }
            }
        }).parse(stream(1, 2, 3, 4), enc());
    }

    private Token createToken(final Long... offsets) {
        final Deque<Long> offsetDeque = new ArrayDeque<Long>(Arrays.asList(offsets));

        return str("test", seq(any("a"), any("b")), new StructSink() {
            @Override
            public void handleStruct(String scopeName, Environment env, Encoding enc, ValueList struct) {
                Assert.assertEquals(offsetDeque.pop().longValue(), struct.tail.head.getOffset());
                Assert.assertNotNull(struct.head);
                Assert.assertNotNull(struct.tail.head);
                Assert.assertNull(struct.tail.tail);
                Assert.assertTrue(struct.tail.head.getName().equals("a"));
                Assert.assertTrue(struct.head.getName().equals("b"));
            }
        });
    }

}
