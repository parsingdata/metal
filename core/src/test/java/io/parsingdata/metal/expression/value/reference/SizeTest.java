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

package io.parsingdata.metal.expression.value.reference;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.size;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static java.lang.Long.MAX_VALUE;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.Shorthand;
import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.ByteStreamSource;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.expression.value.Value;

@RunWith(Parameterized.class)
public class SizeTest {

    @Parameters
    public static Collection<BigInteger> data() {
        return List.of(ZERO, ONE, TEN, BigInteger.valueOf(MAX_VALUE).multiply(TWO));
    }

    private BigInteger size;

    public SizeTest(final BigInteger size) {
        this.size = size;
    }

    @Test
    public void validateSize() {
        ByteStream byteStream = new ByteStream() {
            @Override public byte[] read(BigInteger offset, int length) { return new byte[0]; }
            @Override public boolean isAvailable(BigInteger offset, BigInteger length) { return false; }
            @Override public BigInteger size() { return size; }
        };

        final ImmutableList<Value> offsetCon = size().eval(ParseState.createFromByteStream(byteStream), enc());
        assertEquals(1, offsetCon.size);
        assertEquals(size, offsetCon.head.asNumeric());
    }
}
