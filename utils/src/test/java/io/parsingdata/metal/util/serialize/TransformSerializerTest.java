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
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.Util;
import io.parsingdata.metal.util.serialize.constraint.TransformConstraint;
import io.parsingdata.metal.util.serialize.transform.ParseValueTransformer;

public class TransformSerializerTest {

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

    private static final Token INNER1 = str("INNER1", def("value", con(1)));
    private static final Token INNER2 = str("INNER2", def("value", con(1)));
    private static final Token OUTER = str("OUTER", seq(INNER1, INNER2));

    // unused in actual parsing
    private static final Token INNER3 = str("INNER3", def("value", con(1)));

    public ParseValueTransformer invertBits(final Token context) {
        return new InvertBitTransformer(context);
    }

    @Test
    public void testInvertInner1() throws IOException {
        final byte[] _input = {0, 1};

        final ParseResult result = Util.parse(_input, OUTER);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(_input.length);

        new Serializer()
            .transform(new TransformConstraint(INNER1), "value", invertBits(INNER1))
            .serialize(result, tokenSerializer);
        final byte[] outputData = tokenSerializer.outputData();

        // do not transform second value
        assertArrayEquals(new byte[]{-1, 1}, outputData);
    }

    @Test
    public void testInvertInner2() throws IOException {
        final byte[] input = {0, 1};

        final ParseResult result = Util.parse(input, OUTER);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(input.length);

        new Serializer()
            .transform(new TransformConstraint(INNER2), "value", invertBits(INNER2))
            .serialize(result, tokenSerializer);
        final byte[] outputData = tokenSerializer.outputData();

        // do not transform second value
        assertArrayEquals(new byte[]{0, -2}, outputData);
    }

    @Test
    public void testInvertOuter() throws IOException {
        final byte[] input = {0, 1};

        final ParseResult result = Util.parse(input, OUTER);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(input.length);

        new Serializer()
            .transform(new TransformConstraint(OUTER), "value", invertBits(OUTER))
            .serialize(result, tokenSerializer);
        final byte[] outputData = tokenSerializer.outputData();

        // both values are transformed
        assertArrayEquals(new byte[]{-1, -2}, outputData);
    }

    @Test
    public void testNothingTransforms() throws IOException {
        final byte[] input = {0, 1};

        final ParseResult result = Util.parse(input, OUTER);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(input.length);

        new Serializer()
            .transform(new TransformConstraint(OUTER, INNER3), "value", invertBits(INNER3))
            .serialize(result, tokenSerializer);
        final byte[] outputData = tokenSerializer.outputData();

        assertArrayEquals(new byte[]{0, 1}, outputData);
    }

    @Test
    public void nullConstraint() throws IOException {
        final byte[] input = {0, 1};

        final ParseResult result = Util.parse(input, OUTER);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(input.length);

        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("Argument constraint may not be null.");

        new Serializer()
            .transform(null, "value", invertBits(INNER1))
            .serialize(result, tokenSerializer);
    }

    @Test
    public void nullFieldName() throws IOException {
        final byte[] input = {0, 1};

        final ParseResult result = Util.parse(input, OUTER);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(input.length);

        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("Argument valueName may not be null.");

        new Serializer()
            .transform(new TransformConstraint(OUTER, INNER1), null, invertBits(INNER1))
            .serialize(result, tokenSerializer);
    }

    @Test
    public void nullTransformator() throws IOException {
        final byte[] input = {0, 1};

        final ParseResult result = Util.parse(input, OUTER);
        final CopyTokenSerializer tokenSerializer = new CopyTokenSerializer(input.length);

        _thrown.expect(IllegalArgumentException.class);
        _thrown.expectMessage("Argument transformer may not be null.");

        new Serializer()
            .transform(new TransformConstraint(OUTER, INNER1), "value", null)
            .serialize(result, tokenSerializer);
    }
}
