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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.data.Slice.createFromSource;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.util.InMemoryByteStream;

@RunWith(Parameterized.class)
public class SourceAndSliceTest {

    @Rule public final ExpectedException thrown = ExpectedException.none();

    @Parameter public Source source;

    private static final byte[] DATA = { 0, 1, 2, 3 };

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { new ConstantSource(DATA) },
            { new DataExpressionSource(con(DATA), 0, EMPTY_PARSE_STATE, enc()) },
            { new ByteStreamSource(new InMemoryByteStream(DATA)) },
            { ConcatenatedValueSource.create(ImmutableList.<Value>create(new CoreValue(createFromSource(new ConstantSource(DATA), ZERO, BigInteger.valueOf(2)).get(), enc())).add(new CoreValue(createFromSource(new ConstantSource(DATA), BigInteger.valueOf(2), BigInteger.valueOf(2)).get(), enc()))).get() }
        });
    }

    @Test
    public void validSource() {
        assertTrue(source.isAvailable(ZERO, BigInteger.valueOf(4)));
        assertTrue(source.isAvailable(ONE, BigInteger.valueOf(3)));
        assertTrue(source.isAvailable(BigInteger.valueOf(2), ONE));
        assertTrue(source.isAvailable(BigInteger.valueOf(4), ZERO));
        assertFalse(source.isAvailable(ZERO, BigInteger.valueOf(5)));
        assertFalse(source.isAvailable(BigInteger.valueOf(4), ONE));
        assertFalse(source.isAvailable(BigInteger.valueOf(5), ONE));
        assertFalse(source.isAvailable(BigInteger.valueOf(5), ZERO));
    }

    @Test
    public void validSlice() {
        checkSlice(ZERO, 2);
        checkSlice(ZERO, 4);
        checkSlice(ONE, 3);
        checkSlice(BigInteger.valueOf(2), 1);
        checkSlice(BigInteger.valueOf(2), 2);
        checkSlice(BigInteger.valueOf(4), 0);
    }

    private void checkSlice(final BigInteger offset, final int length) {
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

    @Test
    public void readBeyondEndOfSource() {
        thrown.expect(IllegalStateException.class);
        source.getData(ONE, BigInteger.valueOf(4));
    }

    @Test
    public void readBeyondEndOfSlice() {
        assertFalse(createFromSource(source, ONE, BigInteger.valueOf(4)).isPresent());
    }

    @Test
    public void startReadBeyondEndOfSource() {
        thrown.expect(IllegalStateException.class);
        source.getData(BigInteger.valueOf(5), ZERO);
    }

    @Test
    public void startReadBeyondEndOfSlice() {
        assertFalse(createFromSource(source, BigInteger.valueOf(5), ZERO).isPresent());
    }

    @Test
    public void startReadAtNegativeOffsetSource() {
        thrown.expect(IllegalArgumentException.class);
        source.getData(BigInteger.valueOf(-1L), ONE);
    }

    @Test
    public void startReadAtNegativeOffsetSlice() {
        assertFalse(createFromSource(source, BigInteger.valueOf(-1L), ONE).isPresent());
    }

}
