/**
 * Copyright 2016 National Cyber Security Centre, Netherlands Forensic Institute
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

import static org.junit.Assert.assertArrayEquals;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.Util;

@RunWith(Parameterized.class)
public class CopySerializerTest {

    @Parameter(0)
    public byte[] _inputData;

    @Parameter(1)
    public Token _token;

    private static final Token SUB_TOKEN = seq(
                                               def("ptr1", con(1)),
                                               sub(seq(
                                                       def("value1", con(1)),
                                                       def("ptr2", con(1)),
                                                       sub(def("value2", con(1)),
                                                           ref("ptr2"))),
                                                   ref("ptr1")));

    private static final Token VALUE_PTR = seq(def("value", con(1)), def("ptr2", con(1)));
    private static final Token MULTI_SUB = seq(def("ptr1", con(1)),
                                               sub(seq(VALUE_PTR,
                                                       sub(VALUE_PTR,
                                                           ref("ptr2"))),
                                                   ref("ptr1")));

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new byte[]{42}, def("value", con(1))},
            {new byte[]{42, 43}, def("value", con(2))},
            {new byte[]{42, 43}, rep(def("value", con(1)))},
            {new byte[]{42, 43, 44, 45}, rep(seq(def("value", con(1)), def("value2", con(1))))},
            {new byte[]{2, 84, 42, 1}, SUB_TOKEN},
            {new byte[]{1, 42, 1}, MULTI_SUB}
        });
    }

    @Test
    public void simple() throws IOException {
        final ParseResult result = Util.parse(_inputData, _token);

        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(_inputData.length);

        new Serializer().serialize(result, tokenSerializer);

        final byte[] outputData = tokenSerializer.outputData();

        assertArrayEquals(_inputData, outputData);
    }
}
