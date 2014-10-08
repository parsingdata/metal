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
import java.util.List;

import nl.minvenj.nfi.ddrx.data.ParseResult;
import nl.minvenj.nfi.ddrx.data.ParsedValueList;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SubStructTest {
    
    @Test
    public void offsetTable() throws IOException {
        ParseResult res = rep(sub(any("a"))).parse(stream(1, 3, 9, 5), enc());
        Assert.assertTrue(res.succeeded());
        traverse(5, res.getEnvironment().order);
    }
    
    private void traverse(int next, ParsedValueList list) {
        if (next == 0) {
            Assert.assertNull(list);
        } else {
            Assert.assertEquals(list.head.asNumeric().intValue(), next);
            traverse((int)list.head.getOffset(), list.tail);
        }
    }
    
    @Test
    public void linkedList() throws IOException {
        Token t = sub(seq(def("header", con(1), eq(con(0))),
                          def("next", con(1)),
                          def("footer", con(1), eq(con(1)))), "next");
        ParseResult res = rep(t).parse(stream(0, 8, 1, 42, 0, 12, 1, 84, 0, 4, 1), enc());
                                   /* offset: 0, 1, 2,  3, 4,  5, 6,  7, 8, 9,10
                                    * struct: -------      --------      -------
                                    * ref 1:     +-----------------------^
                                    * ref 2:               ^----------------+
                                    * ref 3:                   +----------------*
                                    */
        Assert.assertTrue(res.succeeded());
        List<Integer> values = Arrays.asList(0, 8, 1, 0, 4, 1, 0, 12, 1);
        ParsedValueList order = res.getEnvironment().order.reverse();
        for (int i = 0; i < values.size(); i++) {
            Assert.assertEquals((int)values.get(i), order.head.asNumeric().intValue());
            order = order.tail;
        }
        Assert.assertNull(order);
    }

}
