/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.data.Slice.createFromSource;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.util.InMemoryByteStream;

public class SourceAndSliceTest {

    private static final byte[] DATA = { 0, 1, 2, 3 };

    public static Collection<Object[]> data() {
        return List.of(new Object[][] {
            { new ConstantSource(DATA) },
            { new DataExpressionSource(con(DATA), 0, EMPTY_PARSE_STATE, enc()) },
            { new ByteStreamSource(new InMemoryByteStream(DATA)) },
            { ConcatenatedValueSource.create(ImmutableList.<Value>create(new CoreValue(createFromSource(new ConstantSource(DATA), ZERO, BigInteger.valueOf(2)).get(), enc())).addHead(new CoreValue(createFromSource(new ConstantSource(DATA), BigInteger.valueOf(2), BigInteger.valueOf(2)).get(), enc()))).get() }
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("data")
    public void validSource(final Source source) {
        assertTrue(source.isAvailable(ZERO, BigInteger.valueOf(4)));
        assertTrue(source.isAvailable(ONE, BigInteger.valueOf(3)));
        assertTrue(source.isAvailable(BigInteger.valueOf(2), ONE));
        assertTrue(source.isAvailable(BigInteger.valueOf(4), ZERO));
        assertFalse(source.isAvailable(ZERO, BigInteger.valueOf(5)));
        assertFalse(source.isAvailable(BigInteger.valueOf(4), ONE));
        assertFalse(source.isAvailable(BigInteger.valueOf(5), ONE));
        assertFalse(source.isAvailable(BigInteger.valueOf(5), ZERO));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void validSlice(final Source source) {
        assertSlice(ZERO, 2, source);
        assertSlice(ZERO, 4, source);
        assertSlice(ONE, 3, source);
        assertSlice(BigInteger.valueOf(2), 1, source);
        assertSlice(BigInteger.valueOf(2), 2, source);
        assertSlice(BigInteger.valueOf(4), 0, source);
    }

    private void assertSlice(final BigInteger offset, final int length, final Source source) {
        assertTrue(compareDataSlices(createFromSource(source, offset, BigInteger.valueOf(length)).get().getData(), offset.intValueExact()));
    }

    private boolean compareDataSlices(byte[] data, int offset) {
        for(int i = 0; i < data.length; i++) {
            if (data[i] != DATA[offset+i]) {
                return false;
            }
        }
        return true;
    }

    @ParameterizedTest
    @MethodSource("data")
    public void readBeyondEndOfSource(final Source source) {
        assertThrows(IllegalStateException.class, () -> source.getData(ONE, BigInteger.valueOf(4)));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void readBeyondEndOfSlice(final Source source) {
        assertFalse(createFromSource(source, ONE, BigInteger.valueOf(4)).isPresent());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void startReadBeyondEndOfSource(final Source source) {
        assertThrows(IllegalStateException.class, () -> source.getData(BigInteger.valueOf(5), ZERO));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void startReadBeyondEndOfSlice(final Source source) {
        assertFalse(createFromSource(source, BigInteger.valueOf(5), ZERO).isPresent());
    }

    @ParameterizedTest
    @MethodSource("data")
    public void startReadAtNegativeOffsetSource(final Source source) {
        assertThrows(IllegalArgumentException.class, () -> source.getData(BigInteger.valueOf(-1L), ONE));
    }

    @ParameterizedTest
    @MethodSource("data")
    public void startReadAtNegativeOffsetSlice(final Source source) {
        assertFalse(createFromSource(source, BigInteger.valueOf(-1L), ONE).isPresent());
    }

}
