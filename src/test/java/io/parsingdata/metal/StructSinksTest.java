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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.TokenDefinitions.any;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Str;
import io.parsingdata.metal.token.StructSink;
import io.parsingdata.metal.token.Token;

@RunWith(JUnit4.class)
public class StructSinksTest {

    private static final String OUTER_NAME = "outer";
    private static final String INNER_NAME = "test";

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
        str(OUTER_NAME, rep(createToken(0L, 2L)), new StructSink() {
            @Override
            public void handleStruct(final String scopeName, final Environment env, final Encoding enc, final ParseGraph struct) {
                // Check top-level is the Str this handleStruct is in.
                Assert.assertTrue(struct.definition instanceof Str);
                Assert.assertEquals(((Str)struct.definition).scope, OUTER_NAME);

                // Check whether it contains the first nested Str
                Assert.assertTrue(struct.head.asGraph().head.getDefinition() instanceof Str);
                Assert.assertEquals(((Str)struct.head.asGraph().head.getDefinition()).scope, INNER_NAME);

                // And the second one
                Assert.assertTrue(struct.head.asGraph().tail.head.getDefinition() instanceof Str);
                Assert.assertEquals(((Str)struct.head.asGraph().tail.head.getDefinition()).scope, INNER_NAME);
            }
        }).parse(stream(1, 2, 3, 4), enc());
    }

    private Token createToken(final Long... offsets) {
        final Deque<Long> offsetDeque = new ArrayDeque<Long>(Arrays.asList(offsets));

        return str(INNER_NAME, seq(any("a"), any("b")), new StructSink() {
            @Override
            public void handleStruct(final String scopeName, final Environment env, final Encoding enc, final ParseGraph struct) {
                // Check top-level is the Str this handleStruct is in.
                Assert.assertTrue(struct.definition instanceof Str);
                Assert.assertEquals(((Str)struct.definition).scope, INNER_NAME);

                // Test for correct offsets and names of values
                Assert.assertEquals(offsetDeque.pop().longValue(), struct.head.asGraph().tail.head.asValue().getOffset());
                Assert.assertTrue(struct.head.asGraph().tail.tail.isEmpty());
                Assert.assertTrue(struct.head.asGraph().tail.head.asValue().getName().equals("a"));
                Assert.assertTrue(struct.head.asGraph().head.asValue().getName().equals("b"));
            }
        });
    }

    @Test
    public void structSinkWithPredicate() throws IOException {
        rep(str("outer", any("a"), new StructSink() {
            @Override
            public void handleStruct(final String scopeName, final Environment env, final Encoding enc, final ParseGraph struct) {
                Assert.assertTrue(struct.tail.isEmpty());
                Assert.assertTrue(struct.head.asValue().asNumeric().intValue() == 2 || struct.head.asValue().asNumeric().intValue() == 4);
            }}, not(eqNum(con(1))))).parse(stream(1, 2, 1, 4), enc());
    }

}
