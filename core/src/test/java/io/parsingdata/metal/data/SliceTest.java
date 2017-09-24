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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.len;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.toByteArray;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.util.InMemoryByteStream;
import io.parsingdata.metal.util.ReadTrackingByteStream;

public class SliceTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Test
    public void lazyRead() {
        final ReadTrackingByteStream stream = new ReadTrackingByteStream(new InMemoryByteStream(toByteArray(1, 2, 3, 0, 0, 4, 1)));
        final Optional<Environment> result =
            seq(def("a", con(3)),
                post(def("b", con(2)), eq(con(0, 0))),
                def("c", con(1)),
                post(def("d", con(1)), eq(con(1)))).parse(new Environment(stream), enc());
        assertTrue(result.isPresent());
        assertTrue(stream.containsAll(3, 4, 6));
        assertTrue(stream.containsNone(0, 1, 2, 5));
    }

    @Test
    public void lazyLength() {
        final ReadTrackingByteStream stream = new ReadTrackingByteStream(new InMemoryByteStream(toByteArray(1, 2, 3, 0, 0, 0, 4, 1)));
        final Optional<Environment> result =
            seq(def("a", con(3)),
                post(def("b", len(last(ref("a")))), eq(con(0, 0, 0))),
                def("c", con(1)),
                post(def("d", len(last(ref("c")))), eq(con(1)))).parse(new Environment(stream), enc());
        assertTrue(result.isPresent());
        assertTrue(stream.containsAll(3, 4, 5, 7));
        assertTrue(stream.containsNone(0, 1, 2, 6));
    }

    @Test
    public void retrieveDataFromSliceWithNegativeLimit() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument limit may not be negative.");
        assertArrayEquals(new byte[] {}, new ConstantSource(new byte[] { 0, 1, 2, 3 }).slice(0, BigInteger.valueOf(4)).getData(BigInteger.valueOf(-1)));
    }


    @Test
    public void retrievePartialDataFromSlice() {
        assertArrayEquals(new byte[] { 0 }, new ConstantSource(new byte[] { 0, 1, 2, 3 }).slice(0, BigInteger.valueOf(4)).getData(BigInteger.ONE));
        assertArrayEquals(new byte[] { 0, 1, 2, 3 }, new ConstantSource(new byte[] { 0, 1, 2, 3 }).slice(0, BigInteger.valueOf(4)).getData(BigInteger.TEN));
    }

}
