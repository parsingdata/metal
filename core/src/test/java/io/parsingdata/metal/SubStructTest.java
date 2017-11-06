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

package io.parsingdata.metal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.data.selection.ByType.getReferences;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.EMPTY_VE;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static junit.framework.TestCase.assertFalse;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.token.Token;

public class SubStructTest {

    public static final Token LINKED_LIST =
        seq("linkedlist",
            def("header", con(1), eq(con(0))),
            def("next", con(1)),
            opt(sub(token("linkedlist"), last(ref("next")))),
            def("footer", con(1), eq(con(1)))
        );

    @Test
    public void linkedList() throws IOException {
        final ParseState parseState = stream(0, 8, 1, 42, 0, 12, 1, 84, 0, 4, 1);
                                  /* offset: 0, 1, 2,  3, 4,  5, 6,  7, 8, 9,10
                                   * struct: -------      --------      -------
                                   * ref 1:     +-----------------------^
                                   * ref 2:               ^----------------+
                                   * ref 3:                   +----------------*
                                   */
        final Optional<ParseState> result = LINKED_LIST.parse(env(parseState, enc()));
        assertTrue(result.isPresent());
        final ParseGraph graph = result.get().order;
        assertEquals(0, getReferences(graph).size); // No cycles

        final ParseGraph first = graph.head.asGraph();
        checkBranch(first, 0, 8);

        final ParseGraph second = first.tail.head.asGraph().head.asGraph().head.asGraph();
        checkBranch(second, 8, 4);

        final ParseGraph third = second.tail.head.asGraph().head.asGraph().head.asGraph();
        checkLeaf(third, 4, 12);
    }

    @Test
    public void linkedListWithSelfReference() throws IOException {
        final ParseState parseState = stream(0, 0, 1);
        final Optional<ParseState> result = LINKED_LIST.parse(env(parseState, enc()));
        assertTrue(result.isPresent());
        final ParseGraph graph = result.get().order;
        assertEquals(1, getReferences(graph).size);

        final ParseGraph first = graph.head.asGraph();
        checkBranch(first, 0, 0);

        final ParseReference reference = first.tail.head.asGraph().head.asGraph().head.asReference();
        checkBranch(reference.resolve(graph).get().asGraph(), 0, 0); // Check cycle
    }

    private ParseGraph startCycle(final int offset) {
        final ParseState parseState = stream(0, 4, 1, 21, 0, 0, 1).seek(BigInteger.valueOf(offset)).get();
        final Optional<ParseState> result = LINKED_LIST.parse(env(parseState, enc()));
        assertTrue(result.isPresent());
        assertEquals(1, getReferences(result.get().order).size);
        return result.get().order;
    }

    @Test
    public void linkedListWithCycle() throws IOException {
        final ParseGraph graph = startCycle(0);

        final ParseGraph first = graph.head.asGraph();
        checkBranch(first, 0, 4);

        final ParseGraph second = first.tail.head.asGraph().head.asGraph().head.asGraph();
        checkBranch(second, 4, 0);

        final ParseReference reference = second.tail.head.asGraph().head.asGraph().head.asReference();
        checkBranch(reference.resolve(graph).get().asGraph(), 0, 4); // Check cycle
    }

    @Test
    public void linkedListWithCycleToLowerOffset() throws IOException {
        final ParseGraph graph = startCycle(4);

        final ParseGraph first = graph.head.asGraph();
        checkBranch(first, 4, 0);

        final ParseGraph second = first.tail.head.asGraph().head.asGraph().head.asGraph();
        checkBranch(second, 0, 4);

        final ParseReference reference = second.tail.head.asGraph().head.asGraph().head.asReference();
        checkBranch(reference.resolve(graph).get().asGraph(), 4, 0); // Check cycle
    }

    private void checkBranch(final ParseGraph graph, final int graphOffset, final int nextOffset) {
        checkValue(graph.head.asGraph().head, 1, graphOffset + 2); // footer
        checkValue(graph.tail.tail.head, nextOffset, graphOffset + 1); // next
        checkValue(graph.tail.tail.tail.head.asGraph().head, 0, graphOffset); // header
    }

    private void checkLeaf(final ParseGraph graph, final int graphOffset, final int nextOffset) {
        checkValue(graph.head.asGraph().head, 1, graphOffset + 2); // footer
        checkValue(graph.tail.tail.head, nextOffset, graphOffset + 1); // next
        checkValue(graph.tail.tail.tail.head.asGraph().head, 0, graphOffset); // header
    }

    private void checkValue(final ParseItem item, final int value, final int offset) {
        assertTrue(item.isValue());
        assertEquals(value, item.asValue().asNumeric().intValueExact());
        assertEquals(offset, item.asValue().slice.offset.intValueExact());
    }

    @Test
    public void errorEmptyAddressList() throws IOException {
        assertFalse(sub(any("a"), ref("b")).parse(env(stream(1, 2, 3, 4))).isPresent());
    }

    @Test
    public void errorEmptyAddress() throws IOException {
        assertFalse(sub(any("a"), EMPTY_VE).parse(env(stream(1, 2, 3, 4))).isPresent());
    }

    @Test
    public void errorNegativeAddress() {
        assertFalse(sub(any("a"), con(-1, signed())).parse(env(stream(1, 2, 3, 4))).isPresent());
    }

}
