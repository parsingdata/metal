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

package io.parsingdata.metal.token;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.math.BigInteger;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Selection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DefTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void scopeWithoutEncoding() {
        assertEquals(1, getValue(def("a", 1).parse(env("scope", stream(1), enc())).get().order, "scope.a").asNumeric().intValueExact());
    }

    @Test
    public void scopeWithEncoding() {
        assertEquals(1, getValue(def("a", 1, signed()).parse(env("scope", stream(1), enc())).get().order, "scope.a").asNumeric().intValueExact());
    }

    @Test
    public void errorEmptyName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name may not be empty.");
        def("", 1);
    }

    @Test
    public void errorNegativeSize() {
        assertFalse(def("negativeSize", con(-1, signed())).parse(env(stream(1))).isPresent());
    }

    @Test
    public void longSize() {
        final Token def = def("data", Long.MAX_VALUE);
        final ByteStream stream = new InfiniteZeroByteStream();
        final ParseState state = ParseState.createFromByteStream(stream, BigInteger.ONE);
        final Environment environment = new Environment(state, enc());

        final ParseState result = def.parse(environment).get();
        final ParseValue data = Selection.getAllValues(result.order, any -> true, 1).head;

        assertThat(data.slice().offset, is(equalTo(BigInteger.ONE)));
        assertThat(data.slice().length, is(equalTo(BigInteger.valueOf(Long.MAX_VALUE))));
    }

    private static final class InfiniteZeroByteStream implements ByteStream {

        @Override
        public byte[] read(final BigInteger offset, final int length) {
            return new byte[length];
        }

        @Override
        public boolean isAvailable(final BigInteger offset, final BigInteger length) {
            return true;
        }
    }

}
