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

package nl.minvenj.nfi.ddrx;

import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.sub;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.data.ParseResult;
import nl.minvenj.nfi.ddrx.data.ParsedValueList;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SubStructTest {
    
    private final Token _token;
    private final Environment _env;
    private final boolean _result;
    private final int[] _values;
    private final int[] _offsets;
    
    @Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "unnamed", rep(sub(any("a"))), stream(1, 3, 9, 5), true, new int[] { 1, 3, 5 }, new int[] { 0, 1, 3 }},
                { "named", rep(sub(seq(def("header", con(1), eq(con(0))),
                                       def("next", con(1)),
                                       def("footer", con(1), eq(con(1)))), "next")),
                                       stream(0, 8, 1, 42, 0, 12, 1, 84, 0, 4, 1), true, new int[] { 0, 8, 1, 0, 4, 1, 0, 12, 1 }, new int[] { 0, 1, 2, 8, 9, 10, 4, 5, 6 }}
                                   /* offset: 0, 1, 2,  3, 4,  5, 6,  7, 8, 9,10
                                    * struct: -------      --------      -------
                                    * ref 1:     +-----------------------^
                                    * ref 2:               ^----------------+
                                    * ref 3:                   +----------------*
                                    */
        });
    }
    
    public SubStructTest(final String desc, final Token token, final Environment env, final boolean result, final int[] values, final int[] offsets) {
        _token = token;
        _env = env;
        _result = result;
        _values = values;
        _offsets = offsets;
    }
    
    @Test
    public void test() throws IOException {
        final ParseResult res = _token.parse(_env, enc());
        Assert.assertEquals(_result, res.succeeded());
        Assert.assertEquals(_values.length, _offsets.length);
        ParsedValueList order = res.getEnvironment().order.reverse();
        for (int i = 0; i < _values.length; i++) {
            Assert.assertEquals(_values[i], order.head.asNumeric().intValue());
            Assert.assertEquals(_offsets[i], order.head.getOffset());
            order = order.tail;
        }
        Assert.assertNull(order);
    }

}
