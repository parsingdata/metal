/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
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
import static io.parsingdata.metal.Util.inflate;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.data.selection.ByType.getReferences;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;
import java.util.zip.Deflater;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.Shorthand;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.expression.value.ValueExpression;
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
            tie(INC_PREV_MOD_100, fold(rev(ref("data")), Shorthand::cat)));

    private static final Token SIMPLE_SEQ = seq(any("a"), any("b"), any("c"));

    @Test
    public void smallContainer() {
        final Optional<ParseState> result = parseContainer();
        assertEquals(5, result.get().offset.intValueExact());
        ImmutableList<ParseValue> parseValues = getAllValues(result.get().order, "value");
        assertEquals(6, (long) parseValues.size());
    }

    private Optional<ParseState> parseContainer() {
        final Optional<ParseState> result = CONTAINER.parse(env(stream(2, 3, 7, 5, 9, 3, 4, 1, 2, 5, 6)));
        assertTrue(result.isPresent());
        return result;
    }

    @Test
    public void checkContainerSource() {
        final Optional<ParseState> result = parseContainer();
        checkFullParse(INC_PREV_MOD_100, fold(ref("value"), Shorthand::cat).eval(result.get(), enc()).head().value());
    }

    private Optional<ParseState> checkFullParse(Token token, byte[] data) {
        final Optional<ParseState> result = token.parse(env(createFromByteStream(new InMemoryByteStream(data)), enc()));
        assertTrue(result.isPresent());
        assertEquals(data.length, result.get().offset.intValueExact());
        return result;
    }

    @Test
    public void increasing() {
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
    public void multiLevelContainers() {
        final byte[] l3Data = generateIncreasing(880);
        final Token l3Token = INC_PREV_MOD_100;
        checkFullParse(l3Token, l3Data);

        final byte[] l2Data = flipBlocks(l3Data, 40);
        final Token l2Token =
            seq(rep(seq(def("left", con(40)), def("right", con(40)))),
                tie(l3Token, fold(cat(ref("right"), ref("left")), Shorthand::cat)));
        checkFullParse(l2Token, l2Data);

        final byte[] l1Data = prefixSize(deflate(l2Data));
        final Token l1Token =
            seq(def("size", con(4)),
                def("data", last(ref("size"))),
                tie(l2Token, inflate(last(ref("data")))));
        final Optional<ParseState> result = checkFullParse(l1Token, l1Data);
        assertEquals(80, result.get().order.head.asGraph().head.asGraph().head.asGraph().head.asGraph().head.asGraph().head.asGraph().head.asValue().asNumeric().intValueExact());
    }

    private byte[] flipBlocks(byte[] input, int blockSize) {
        if ((input.length % (blockSize * 2)) != 0) { throw new UnsupportedOperationException("Not supported."); }
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

    @Test
    public void tieAndSubOnSameData() {
        final Token nestedSeq =
            seq(def("d", con(3)),
                tie(SIMPLE_SEQ, ref("d")),
                sub(SIMPLE_SEQ, con(0)));
        final Optional<ParseState> result = nestedSeq.parse(env(stream(1, 2, 3)));
        assertTrue(result.isPresent());
        ImmutableList<Optional<ParseItem>> optionals = getReferences(result.get().order);
        assertEquals(0, (long) optionals.size());
    }

    @Test
    public void multiTie() {
        final Token multiTie =
            seq(def("d", con(3)),
                def("d", con(3)),
                tie(SIMPLE_SEQ, ref("d")));
        final Optional<ParseState> result = multiTie.parse(env(stream(1, 2, 3, 1, 2, 3)));
        assertTrue(result.isPresent());
        ImmutableList<Optional<ParseItem>> optionals = getReferences(result.get().order);
        assertEquals(0, (long) optionals.size());
        final String[] names = { "a", "b", "c", "d" };
        for (String name : names) {
            ImmutableList<ParseValue> values = getAllValues(result.get().order, name);
            assertEquals(2, (long) values.size());
        }
    }

    @Test
    public void tieWithDuplicate() {
        final ValueExpression refD = ref("d");
        final Token duplicateTie =
            seq(def("d", con(3)),
                tie(SIMPLE_SEQ, refD),
                tie(SIMPLE_SEQ, refD));
        final Optional<ParseState> result = duplicateTie.parse(env(stream(1, 2, 3)));
        assertTrue(result.isPresent());
        ImmutableList<Optional<ParseItem>> optionals = getReferences(result.get().order);
        assertEquals(0, (long) optionals.size());
        ImmutableList<ParseValue> parseValues = getAllValues(result.get().order, "d");
        assertEquals(1, (long) parseValues.size());
        final String[] names = { "a", "b", "c" };
        for (String name : names) {
            ImmutableList<ParseValue> values = getAllValues(result.get().order, name);
            assertEquals(2, (long) values.size());
        }
    }

    @Test
    public void tieWithEmptyListFromDataExpression() {
        final Token token = seq(any("a"), tie(any("b"), last(ref("c"))));
        final Optional<ParseState> result = token.parse(env(stream(0)));
        assertTrue(result.isPresent());
        assertTrue(result.get().order.head.asGraph().head.asGraph().isEmpty());
    }

    @Test
    public void tieFail() {
        final Token token = seq(def("a", con(1), eq(con(0))), tie(def("b", con(1), eq(con(1))), last(ref("a"))));
        assertFalse(token.parse(env(stream(0))).isPresent());
    }

    @Test
    public void tieWithEmptyValueFromDataExpression() {
        final Token token = seq(any("a"), tie(any("b"), div(con(1), con(0))));
        assertFalse(token.parse(env(stream(0))).isPresent());
    }

    @Test
    public void tieOnConstant() {
        final Token strictSeq =
            seq(def("a", con(1), eq(con(1))),
                def("b", con(1), eq(con(2))),
                def("c", con(1), eq(con(3))));
        assertTrue(tie(strictSeq, con(1, 2, 3)).parse(env(stream())).isPresent());
        assertFalse(tie(strictSeq, con(1, 2, 4)).parse(env(stream())).isPresent());
    }

}
