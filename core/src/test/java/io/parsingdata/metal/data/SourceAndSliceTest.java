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

package io.parsingdata.metal.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.io.IOException;
import java.io.UncheckedIOException;
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

import io.parsingdata.metal.util.InMemoryByteStream;

@RunWith(Parameterized.class)
public class SourceAndSliceTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Parameter public Source source;

    private static final byte[] DATA = { 0, 1, 2, 3 };

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { new ConstantSource(DATA) },
            { new DataExpressionSource(con(DATA), 0, ParseGraph.EMPTY, enc()) },
            { new ByteStreamSource(new InMemoryByteStream(DATA) ) }
        });
    }

    @Test
    public void validSource() {
        assertTrue(source.isAvailable(0, BigInteger.valueOf(4)));
        assertTrue(source.isAvailable(1, BigInteger.valueOf(3)));
        assertTrue(source.isAvailable(2, BigInteger.valueOf(1)));
        assertTrue(source.isAvailable(4, BigInteger.valueOf(0)));
        assertFalse(source.isAvailable(0, BigInteger.valueOf(5)));
        assertFalse(source.isAvailable(4, BigInteger.valueOf(1)));
        assertFalse(source.isAvailable(5, BigInteger.valueOf(1)));
        assertFalse(source.isAvailable(5, BigInteger.valueOf(0)));
    }

    @Test
    public void validSlice() {
        checkSlice(0, 4);
        checkSlice(1, 3);
        checkSlice(2, 1);
        checkSlice(4, 0);
    }

    @Test
    public void readBeyondEndOfSource() throws IOException {
        thrown.expect(IOException.class);
        source.getData(1, BigInteger.valueOf(4));
    }

    @Test
    public void readBeyondEndOfSlice() {
        thrown.expect(UncheckedIOException.class);
        source.slice(1, BigInteger.valueOf(4)).getData();
    }

    @Test
    public void startReadBeyondEndOfSource() throws IOException {
        thrown.expect(IOException.class);
        source.getData(5, BigInteger.valueOf(0));
    }

    @Test
    public void startReadBeyondEndOfSlice() {
        thrown.expect(UncheckedIOException.class);
        source.slice(5, BigInteger.valueOf(0)).getData();
    }

    private void checkSlice(final int offset, final int length) {
        assertTrue(compareDataSlices(source.slice(offset, BigInteger.valueOf(length)).getData(), offset));
    }

    private boolean compareDataSlices(byte[] data, int offset) {
        for(int i = 0; i < data.length; i++) {
            if (data[i] != DATA[offset+i]) { return false; }
        }
        return true;
    }

}
