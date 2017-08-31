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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Util.createFromBytes;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConstantSliceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullInput() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument data may not be null.");
        createFromBytes(null);
    }

    @Test
    public void readBeyondSourceSize() throws IOException {
        thrown.expect(IOException.class);
        thrown.expectMessage("Data to read is not available.");
        final byte[] input = { 1, 2, 3, 4 };
        final Slice slice = createFromBytes(input);
        final byte[] outputBeyond = slice.source.getData(4, BigInteger.ONE);
        assertEquals(0, outputBeyond.length);
    }

    @Test
    public void checkData() {
        final byte[] input = { 1, 2, 3, 4 };
        final Slice slice = createFromBytes(input);
        final byte[] output = slice.getData();
        assertEquals(input.length, output.length);
        assertTrue(Arrays.equals(input, output));
    }

    @Test
    public void checkSource() throws IOException {
        final byte[] input = { 1, 2, 3, 4 };
        final Slice slice = createFromBytes(input);
        final byte[] output = slice.source.getData(0, BigInteger.valueOf(4));
        assertEquals(input.length, output.length);
        assertTrue(Arrays.equals(input, output));
    }

}
