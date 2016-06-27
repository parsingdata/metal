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
import static io.parsingdata.metal.Shorthand.str;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.Util;
import io.parsingdata.metal.util.serialize.transform.InvertBitTransformer;

@RunWith(Parameterized.class)
public class InvertBitsCopySerializerTest {

    @Parameter(0)
    public byte[] _inputData;

    @Parameter(1)
    public Token _token;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new byte[]{42}, def("value", con(1))},
            {new byte[]{42}, str("str", def("value", con(1)))},
        });
    }

    @Test
    public void testInvert() throws IOException {
        final ParseResult result = Util.parse(_inputData, _token);
        final InvertBitTransformer transformer = new InvertBitTransformer(ParseGraph.NONE);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(_inputData.length);

        new Serializer()
            .transform("*", transformer)
            .serialize(result, tokenSerializer);
        final byte[] outputData = tokenSerializer.outputData();

        assertArrayEquals(invertBits(_inputData), outputData);
    }

    @Test
    public void testDoubleInvert() throws IOException {
        final ParseResult result = Util.parse(_inputData, _token);
        final InvertBitTransformer transformer = new InvertBitTransformer(ParseGraph.NONE);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(_inputData.length);

        new Serializer()
            .transform("*", transformer)
            .transform("*", transformer)
            .serialize(result, tokenSerializer);

        final byte[] outputData = tokenSerializer.outputData();

        assertArrayEquals(_inputData, outputData);
    }

    private byte[] invertBits(final byte[] input) {
        for (int i = 0; i < input.length; i++) {
            input[i] = (byte) ~input[i];
        }
        return input;
    }
}
