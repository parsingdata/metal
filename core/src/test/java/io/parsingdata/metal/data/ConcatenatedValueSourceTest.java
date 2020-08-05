/*
 * Copyright 2013-2020 Netherlands Forensic Institute
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

import static io.parsingdata.metal.data.Slice.createFromSource;
import static io.parsingdata.metal.expression.value.ConstantFactory.createFromBytes;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.Value;

@RunWith(Parameterized.class)
public class ConcatenatedValueSourceTest {

    @Parameter public String description;
    @Parameter(1) public int offset;
    @Parameter(2) public int length;

    public static final ConcatenatedValueSource cvs = ConcatenatedValueSource.create(createValues()).get();

    private static ImmutableList<Value> createValues() {
        final byte[] twoSliceSource = new byte[] { -1, -1, 5, 6, 7, 8, 9, -1, -1, 10, 11, 12, 13, 14, -1, -1 };
        return ImmutableList
            .create(createFromBytes(new byte[] { 0, 1, 2, 3, 4 }, enc()))
            .add(new CoreValue(createFromSource(new ConstantSource(twoSliceSource), BigInteger.valueOf(2), BigInteger.valueOf(5)).get(), enc()))
            .add(new CoreValue(createFromSource(new ConstantSource(twoSliceSource), BigInteger.valueOf(9), BigInteger.valueOf(5)).get(), enc()))
            .add(createFromBytes(new byte[] { 15, 16, 17, 18, 19 }, enc()))
            .add(createFromBytes(new byte[] { 20, 21, 22, 23, 24 }, enc()));
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "full", 0, 25 },                // [XXXXX][XXXXX][XXXXX][XXXXX][XXXXX]
            { "none", 0, 0 },                 // [.....][.....][.....][.....][.....]
            { "full(0)", 0, 5 },              // [XXXXX][.....][.....][.....][.....]
            { "part(1)", 1, 3 },              // [.XXX.][.....][.....][.....][.....]
            { "full(0) to part(1)", 0, 8 },   // [XXXXX][XXX..][.....][.....][.....]
            { "part(0) to part(1)", 2, 6 },   // [..XXX][XXX..][.....][.....][.....]
            { "part(0) to full(1)", 2, 8 },   // [..XXX][XXXXX][.....][.....][.....]
            { "full(0) to part(2)", 0, 12 },  // [XXXXX][XXXXX][XX...][.....][.....]
            { "full(0) to full(2)", 0, 15 },  // [XXXXX][XXXXX][XXXXX][.....][.....]
            { "part(0) to part(2)", 3, 8 },   // [...XX][XXXXX][X....][.....][.....]
            { "part(0) to full(2)", 3, 12 },  // [...XX][XXXXX][XXXXX][.....][.....]
            { "full(0) to part(3)", 0, 17 },  // [XXXXX][XXXXX][XXXXX][XX...][.....]
            { "part(0) to full(3)", 4, 16 },  // [....X][XXXXX][XXXXX][XXXXX][.....]
            { "full(0) to part(4)", 0, 22 },  // [XXXXX][XXXXX][XXXXX][XXXXX][XX...]
            { "part(0) to part(4)", 3, 20 },  // [...XX][XXXXX][XXXXX][XXXXX][XXX..]
            { "part(0) to full(4)", 4, 21 },  // [....X][XXXXX][XXXXX][XXXXX][XXXXX]
            { "full(1) to full(3)", 5, 15 },  // [.....][XXXXX][XXXXX][XXXXX][.....]
            { "part(1) to full(3)", 7, 13 },  // [.....][..XXX][XXXXX][XXXXX][.....]
            { "part(1) to part(3)", 8, 11 },  // [.....][...XX][XXXXX][XXXX.][.....]
            { "full(2) to full(4)", 10, 15 }, // [.....][.....][XXXXX][XXXXX][XXXXX]
            { "full(2) to part(4)", 10, 12 }, // [.....][.....][XXXXX][XXXXX][XX...]
            { "part(2) to full(4)", 12, 13 }, // [.....][.....][..XXX][XXXXX][XXXXX]
            { "part(2) to part(4)", 11, 11 }, // [.....][.....][.XXXX][XXXXX][XX...]
            { "full(4)", 20, 5 },             // [.....][.....][.....][.....][XXXXX]
            { "part(4)", 21, 4 }              // [.....][.....][.....][.....][.XXXX]
        });
    }

    @Test
    public void checkData() {
        byte[] data = cvs.getData(BigInteger.valueOf(offset), BigInteger.valueOf(length));
        assertEquals(length, data.length);
        for (int i = 0; i < length; i++) {
            assertEquals(offset+i, data[i]);
        }
    }

}
