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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.opt;
import static nl.minvenj.nfi.metal.Shorthand.ref;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.Shorthand.sub;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.data.ParseValueList;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.token.Token;

@RunWith(Parameterized.class)
public class SubStructTest {

    private final Token _token;
    private final Environment _env;
    private final boolean _result;
    private final int[] _values;
    private final int[] _offsets;
    private final int _refCount;

    private static class LinkedList extends Token {

        private final Token struct;

        public LinkedList(final Encoding enc) {
            super(enc);
            struct =
                seq(def("header", con(1), eq(con(0))),
                def("next", con(1)),
                opt(sub(this, ref("next"))),
                def("footer", con(1), eq(con(1))));
        }

        @Override
        protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
            return struct.parse(scope, env, enc);
        }

    }

    @Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "linkedlist", new LinkedList(enc()), stream(0, 8, 1, 42, 0, 12, 1, 84, 0, 4, 1), true, new int[] { 0, 8, 0, 4, 0, 12, 1, 1, 1 }, new int[] { 0, 1, 8, 9, 4, 5, 6, 10, 2 }, 0 },
                                               /* offset: 0, 1, 2,  3, 4,  5, 6,  7, 8, 9,10
                                                * struct: -------      --------      -------
                                                * ref 1:     +-----------------------^
                                                * ref 2:               ^----------------+
                                                * ref 3:                   +----------------*
                                                */
            { "linkedlist with self reference", new LinkedList(enc()), stream(0, 0, 1), true, new int[] { 0, 0, 1 }, new int[] { 0, 1, 2 }, 1 },
            { "linkedlist with cycle", new LinkedList(enc()), stream(0, 4, 1, 21, 0, 0, 1), true, new int[] { 0, 4, 0, 0, 1, 1 }, new int[] { 0, 1, 4, 5, 6, 2 }, 1 }
        });
    }

    public SubStructTest(final String desc, final Token token, final Environment env, final boolean result, final int[] values, final int[] offsets, final int refCount) {
        _token = token;
        _env = env;
        _result = result;
        _values = values;
        _offsets = offsets;
        _refCount = refCount;
    }

    @Test
    public void test() throws IOException {
        final ParseResult res = _token.parse(_env, enc());
        Assert.assertEquals(_result, res.succeeded());
        Assert.assertEquals(_values.length, _offsets.length);
        ParseValueList order = res.getEnvironment().order.flatten().reverse();
        for (int i = 0; i < _values.length; i++) {
            Assert.assertEquals(_values[i], order.head.asNumeric().intValue());
            Assert.assertEquals(_offsets[i], order.head.getOffset());
            order = order.tail;
        }
        Assert.assertTrue(order.isEmpty());
    }

    @Test
    public void testRefs() throws IOException {
        final ParseResult res = _token.parse(_env, enc());
        Assert.assertEquals(_result, res.succeeded());
        Assert.assertEquals(_refCount, res.getEnvironment().order.getRefs().size);
    }

}
