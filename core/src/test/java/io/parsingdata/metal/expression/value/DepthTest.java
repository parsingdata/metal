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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.depth;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.util.EncodingFactory.le;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValueList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.EnvironmentFactory;

public class DepthTest {

    @Test
    public void testDepth() throws IOException {
        assertDepth(seq(
            def("root", 0),
            pre(
                def("value", 0),
                eqNum(depth("dir"), con(0)))), 1);

        assertDepth(seq(
            def("root", 0),
            str("dir",
                pre(
                    def("value", 0),
                    eqNum(depth("dir"), con(1))))), 1);

        assertDepth(seq(
            def("root", 0),
            str("dir",
                pre(
                    def("value", 0),
                    eqNum(depth("dir"), con(1)))),
            str("dir",
                pre(
                    def("value", 0),
                    eqNum(depth("dir"), con(1))))), 2);

        assertDepth(seq(
            def("root", 0),
            str("dir",
                pre(
                    def("value", 0),
                    eqNum(depth("dir"), con(2))))), 0);

        assertDepth(seq(
            def("root", 0),
            str("dir", str("dir", str("dir", str("dir", str("dir", str("dir", str("dir",
                pre(
                    def("value", 0),
                    eqNum(depth("dir"), con(7))))))))))), 1);

        assertDepth(seq(
            def("root", 0),
            str("dir", str("dir", str("dir", str("dir", str("dir", str("dir", str("dir",
                pre(
                    def("value", 0),
                    eqNum(depth("dir"), con(6))))))))))), 0);
    }

    private void assertDepth(final Token token, final int count) throws IOException {
        final Environment env = EnvironmentFactory.stream();
        final ParseResult result = token.parse(env, le());
        assertTrue(result.succeeded());

        final ParseValueList list = result.getEnvironment().order.getAll("value");
        assertNotNull(list);
        assertEquals(count, list.size);
    }

    @Test
    public void testDirectory() throws IOException {
        final int[] root = {
            2, //  0. number of entries
            3, //  1. Pointer to directory 1.1
            4, //  2. Pointer to directory 1.2

            'A', //  3. Name of directory 1.1
            'B', //  4. Name of directory 1.2
        };

        final Token directory = new Directory();

        final Environment env = EnvironmentFactory.stream(root);
        final ParseResult result = directory.parse(env, le());
        assertTrue(result.succeeded());

        final ParseValueList list = result.getEnvironment().order.getAll("name");
        assertEquals(2, list.size);

    }

    static class Directory extends Token {

        private final Token _struct;

        Directory() {
            super(null);
            _struct = str("dir",
                seq(
                    pre(
                        seq(
                            def("count", 1),
                            sub(this, ref("count"))
                        ), eqNum(depth("dir"), con(1))
                    ),
                    pre(
                        def("name", 1),
                        gtNum(depth("dir"), con(1))
                    )));
        }

        @Override
        protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
            return _struct.parse(scope, env, enc);
        }
    }
}
