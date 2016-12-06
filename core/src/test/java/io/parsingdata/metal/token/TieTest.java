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

package io.parsingdata.metal.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.CAT_REDUCER;
import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.elvis;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.rev;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.Util.bytesToSlice;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.data.selection.ByType.getReferences;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.ValueOperation;
import io.parsingdata.metal.util.InMemoryByteStream;

public class TieTest {

    // Starts at 1, then increases with 1, modulo 100.
    private static final Token INC_PREV_MOD_100 =
        rep(def("value", 1, eq(mod(add(elvis(nth(rev(ref("value")), con(1)), con(0)), con(1)), con(100)))));

    private static final Token CONTAINER =
        seq(def("blockSize", 1),
            def("tableSize", 1),
            repn(any("offset"), last(ref("tableSize"))),
            sub(def("data", last(ref("blockSize"))), ref("offset")),
            tie(INC_PREV_MOD_100, fold(rev(ref("data")), CAT_REDUCER)));

    @Test
    public void smallContainer() throws IOException {
        final ParseResult result = parseContainer();
        assertEquals(5, result.environment.offset);
        assertEquals(6, getAllValues(result.environment.order, "value").size);
    }

    private ParseResult parseContainer() throws IOException {
        final ParseResult result = CONTAINER.parse(stream(2, 3, 7, 5, 9, 3, 4, 1, 2, 5, 6), enc());
        assertTrue(result.succeeded);
        return result;
    }

    @Test
    public void checkContainerSource() throws IOException {
        final ParseResult result = parseContainer();
        checkFullParse(INC_PREV_MOD_100, fold(ref("value"), CAT_REDUCER).eval(result.environment, enc()).head.get().getValue());
    }

    private ParseResult checkFullParse(Token token, byte[] data) throws IOException {
        final ParseResult result = token.parse(new Environment(new InMemoryByteStream(data)), enc());
        assertTrue(result.succeeded);
        assertEquals(data.length, result.environment.offset);
        return result;
    }

    @Test
    public void increasing() throws IOException {
        checkFullParse(INC_PREV_MOD_100, generateIncreasing(1024));
    }

    private static byte[] generateIncreasing(final int size) {
        final byte[] data = new byte[size];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)((i+1) % 100);
        }
        return data;
    }

    @Test
    public void multiLevelContainers() throws IOException {
        final byte[] l3Data = generateIncreasing(880);
        final Token l3Token = INC_PREV_MOD_100;
        checkFullParse(l3Token, l3Data);

        final byte[] l2Data = flipBlocks(l3Data, 40);
        final Token l2Token =
            seq(rep(seq(def("left", con(40)), def("right", con(40)))),
                tie(l3Token, fold(cat(ref("right"), ref("left")), CAT_REDUCER)));
        checkFullParse(l2Token, l2Data);

        final byte[] l1Data = prefixSize(deflate(l2Data));
        final Token l1Token =
            seq(def("size", con(4)),
                def("data", last(ref("size"))),
                tie(l2Token, inflate(last(ref("data")))));
        final ParseResult result = checkFullParse(l1Token, l1Data);
        assertEquals(80, result.environment.order.head.asGraph().head.asGraph().head.asGraph().head.asGraph().head.asGraph().head.asValue().asNumeric().intValue());
    }

    private byte[] flipBlocks(byte[] input, int blockSize) {
        if ((input.length % (blockSize * 2)) != 0) { throw new RuntimeException("Not supported."); }
        final byte[] output = input.clone();
        for (int i = 0; i < output.length; i+= blockSize * 2) {
            for (int j= 0; j < blockSize; j++) {
                output[j+i] = input[j+i+blockSize];
                output[j+i+blockSize] = input[j+i];
            }
        }
        return output;
    }

    private byte[] deflate(byte[] data) {
        final Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(data);
        deflater.finish();
        final byte[] buffer = new byte[data.length * 2];
        int length = deflater.deflate(buffer);
        deflater.end();
        final byte[] output = new byte[length];
        System.arraycopy(buffer, 0, output, 0, length);
        return output;
    }

    private byte[] prefixSize(byte[] data) {
        final byte[] output = new byte[data.length + 4];
        output[0] = (byte)((data.length & 0xff000000) >> 24);
        output[1] = (byte)((data.length & 0xff0000) >> 16);
        output[2] = (byte)((data.length & 0xff00) >> 8);
        output[3] = (byte) (data.length & 0xff);
        System.arraycopy(data, 0, output, 4, data.length);
        return output;
    }

    public static ValueExpression inflate(final ValueExpression target) {
        return new UnaryValueExpression(target) {
            @Override
            public OptionalValue eval(final Value value, final Environment environment, final Encoding encoding) {
                return value.operation(new ValueOperation() {
                    @Override
                    public OptionalValue execute(final Value value) {
                        final Inflater inf = new Inflater(true);
                        inf.setInput(value.getValue());
                        final byte[] dataReceiver = new byte[512];
                        final ByteArrayOutputStream out = new ByteArrayOutputStream();
                        while(!inf.finished()) {
                            try {
                                final int processed = inf.inflate(dataReceiver);
                                out.write(dataReceiver, 0, processed);
                            } catch (final DataFormatException e) {
                                return OptionalValue.empty();
                            }
                        }
                        return OptionalValue.of(new Value(bytesToSlice(out.toByteArray()), encoding));
                    }
                });
            }
        };
    }

    @Test
    public void subInTie() throws IOException {
        final Token simpleSeq = seq(any("a"), any("b"), any("c"));
        final Token nestedSeq =
            seq(simpleSeq,
                def("d", con(3)),
                tie(sub(simpleSeq, con(0)), last(ref("d"))));
        final ParseResult result = nestedSeq.parse(stream(1, 2, 3, 1, 2, 3), enc());
        assertTrue(result.succeeded);
        assertEquals(0, getReferences(result.environment.order).size);
    }

}
