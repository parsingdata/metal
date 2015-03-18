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
import static nl.minvenj.nfi.metal.Shorthand.not;
import static nl.minvenj.nfi.metal.Shorthand.pre;
import static nl.minvenj.nfi.metal.Shorthand.ref;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.Shorthand.sub;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseGraph;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.data.ParseValueList;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.token.Token;

@RunWith(JUnit4.class)
public class TreeTest {

    private final ParseResult _result;

    public TreeTest() throws IOException {
        _result = new Token(null) { @Override protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
            return seq(def("head", con(1), eq(con(9))),
                       def("nr", con(1)),
                       def("left", con(1)),
                       pre(sub(this, ref("left")), not(eq(ref("left"), con(0)))),
                       def("right", con(1)),
                       pre(sub(this, ref("right")), not(eq(ref("right"), con(0))))).parse(scope, env, enc);
            }
        }.parse(stream(9, 0, 6, 10, 8, 8, 9, 1, 16, 20, 9, 2, 24, 28, 8, 8, 9, 3, 0, 0, 9, 4, 0, 0, 9, 5, 0, 0, 9, 6, 0, 0), enc());
    }

    @Test
    public void checkTree() {
        Assert.assertTrue(_result.succeeded());
        checkStruct(_result.getEnvironment().order.reverse(), 0, 0);
    }

    private void checkStruct(final ParseGraph graph, final long offset, final int id) {
        Assert.assertTrue(graph.head.isValue());
        Assert.assertEquals(9, graph.head.getValue().asNumeric().intValue());
        Assert.assertEquals(offset, graph.head.getValue().getOffset());
        Assert.assertTrue(graph.tail.head.isValue());
        Assert.assertEquals(id, graph.tail.head.getValue().asNumeric().intValue());
    }

    @Test
    public void checkTreeFlat() {
        Assert.assertTrue(_result.succeeded());
        final ParseValueList nrs = _result.getEnvironment().order.flatten().getAll("nr");
        for (int i = 0; i < 7; i++) {
            Assert.assertTrue(contains(nrs, i));
        }
    }

    private boolean contains(final ParseValueList nrs, final int i) {
        if (nrs.head.asNumeric().intValue() == i) { return true; }
        if (nrs.tail != null) { return contains(nrs.tail, i); }
        return false;
    }

}
