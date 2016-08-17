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

package io.parsingdata.metal.util.serialize;

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.nod;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.util.TestUtil.range;
import static io.parsingdata.metal.util.TestUtil.ranges;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.Util;
import io.parsingdata.metal.util.serialize.process.CoverageAnalyzer;

@RunWith(Parameterized.class)
public class CoverageSerializerTest {

    @Parameter(0)
    public byte[] _input;

    @Parameter(1)
    public List<Range<Long>> _expected;

    @Parameter(2)
    public Token _token;

    private static final Token SUB_TOKEN =
        seq(
            def("ptr1", 1),
            sub(seq(
                    def("value1", 1),
                    def("ptr2", 1),
                    sub(def("value2", 1),
                        ref("ptr2"))),
                ref("ptr1")));

    private static final Token VALUE_PTR = seq(def("value", 1), def("ptr2", 1));

    private static final Token MULTI_SUB =
        seq(
            def("ptr1", 1),
            sub(seq(VALUE_PTR,
                    sub(VALUE_PTR,
                        ref("ptr2"))),
                ref("ptr1")));

    private static final Token SEQ_WITH_NOD =
        seq(
            repn(
                 def("value", 1),
                 con(2)),
            nod(con(1)),
            rep(
                def("value", 1)));

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new byte[]{42}, ranges(range(0, 1)), def("value", 1)},
            {new byte[]{42, 1}, ranges(range(0, 1)), def("value", 1)},
            {new byte[]{42, 43}, ranges(range(0, 2)), rep(def("value", 1))},
            {new byte[]{42, 43, 1, 44, 45}, ranges(range(0, 2), range(3, 5)), SEQ_WITH_NOD},
            {new byte[]{2, 84, 42, 1}, ranges(range(0, 4)), SUB_TOKEN},
            {new byte[]{1, 42, 1}, ranges(range(0, 3)), MULTI_SUB}
        });
    }

    @Test
    public void simple() throws IOException {
        final ParseResult result = Util.parse(_input, _token);

        final CoverageAnalyzer analyzer = new CoverageAnalyzer();
        new Serializer().serialize(result, analyzer);

        assertEquals(_expected, analyzer.analysis().ranges());
    }
}