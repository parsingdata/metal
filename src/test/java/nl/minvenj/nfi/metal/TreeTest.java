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
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.pre;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.sub;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.data.ParseResult;
import nl.minvenj.nfi.ddrx.data.ParsedValueList;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TreeTest {

    @Test
    public void parseTree2() throws IOException {
        final ParseResult result = new Token(null) { @Override protected ParseResult parseImpl(String scope, Environment env, Encoding enc) throws IOException {
                return seq(def("head", con(1), eq(con(9))),
                           def("nr", con(1)),
                           def("left", con(1)),
                           pre(sub(this, ref("left")), not(eq(ref("left"), con(0)))),
                           def("right", con(1)),
                           pre(sub(this, ref("right")), not(eq(ref("right"), con(0))))).parse(scope, env, enc);
            }}.parse(stream(9, 0, 6, 10, 8, 8, 9, 1, 16, 20, 9, 2, 24, 28, 8, 8, 9, 3, 0, 0, 9, 4, 0, 0, 9, 5, 0, 0, 9, 6, 0, 0), enc());
            Assert.assertTrue(result.succeeded());
            ParsedValueList nrs = result.getEnvironment().order.getAll("nr");
            for (int i = 0; i < 7; i++) {
                Assert.assertTrue(contains(nrs, i));
            }
    }

    private boolean contains(ParsedValueList nrs, int i) {
        if (nrs.head.asNumeric().intValue() == i) { return true; }
        if (nrs.tail != null) { return contains(nrs.tail, i); }
        return false;
    }

}
