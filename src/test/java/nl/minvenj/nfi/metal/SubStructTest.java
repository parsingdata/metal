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

import org.junit.Assert;
import org.junit.Test;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseGraph;
import nl.minvenj.nfi.metal.data.ParseItem;
import nl.minvenj.nfi.metal.data.ParseRef;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.token.Token;

public class SubStructTest {

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

    @Test
    public void linkedList() throws IOException {
        final Token token = new LinkedList(enc());
        final Environment env = stream(0, 8, 1, 42, 0, 12, 1, 84, 0, 4, 1);
                            /* offset: 0, 1, 2,  3, 4,  5, 6,  7, 8, 9,10
                             * struct: -------      --------      -------
                             * ref 1:     +-----------------------^
                             * ref 2:               ^----------------+
                             * ref 3:                   +----------------*
                             */
        final ParseResult res = token.parse(env, enc());
        Assert.assertTrue(res.succeeded());
        final ParseGraph out = res.getEnvironment().order;
        Assert.assertEquals(0, out.getRefs().size); // No cycles

        final ParseGraph first = out.head.asGraph();
        checkBranch(first, 0, 8);

        final ParseGraph second = first.tail.head.asGraph().head.asGraph().head.asGraph();
        checkBranch(second, 8, 4);

        final ParseGraph third = second.tail.head.asGraph().head.asGraph().head.asGraph();
        checkLeaf(third, 4, 12);
    }

    @Test
    public void linkedListWithSelfReference() throws IOException {
        final Token token = new LinkedList(enc());
        final Environment env = stream(0, 0, 1);
        final ParseResult res = token.parse(env, enc());
        Assert.assertTrue(res.succeeded());
        final ParseGraph out = res.getEnvironment().order;
        Assert.assertEquals(1, out.getRefs().size);

        final ParseGraph first = out.head.asGraph();
        checkBranch(first, 0, 0);

        final ParseRef ref = first.tail.head.asGraph().head.asRef();
        checkBranch(ref.resolve(out), 0, 0); // Check cycle
    }

    @Test
    public void linkedListWithCycle() throws IOException {
        final Token token = new LinkedList(enc());
        final Environment env = stream(0, 4, 1, 21, 0, 0, 1);
        final ParseResult res = token.parse(env, enc());
        Assert.assertTrue(res.succeeded());
        final ParseGraph out = res.getEnvironment().order;
        Assert.assertEquals(1, out.getRefs().size);

        final ParseGraph first = out.head.asGraph();
        checkBranch(first, 0, 4);

        final ParseGraph second = first.tail.head.asGraph().head.asGraph().head.asGraph();
        checkBranch(second, 4, 0);

        final ParseRef ref = second.tail.head.asGraph().head.asRef();
        checkBranch(ref.resolve(out), 0, 4); // Check cycle
    }

    private void checkBranch(final ParseGraph graph, final int graphOffset, final int nextOffset) {
        checkValue(graph.head, 1, graphOffset + 2); // footer
        checkValue(graph.tail.tail.head, nextOffset, graphOffset + 1); // next
        checkValue(graph.tail.tail.tail.head, 0, graphOffset); // header
    }

    private void checkLeaf(final ParseGraph graph, final int graphOffset, final int nextOffset) {
        checkValue(graph.head, 1, graphOffset + 2); // footer
        checkValue(graph.tail.head, nextOffset, graphOffset + 1); // next
        checkValue(graph.tail.tail.head, 0, graphOffset); // header
    }

    private void checkValue(final ParseItem item, final int value, final int offset) {
        Assert.assertTrue(item.isValue());
        Assert.assertEquals(value, item.asValue().asNumeric().intValue());
        Assert.assertEquals(offset, item.asValue().getOffset());
    }

}
