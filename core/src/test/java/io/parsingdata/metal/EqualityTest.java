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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.AutoEqualityTest.DUMMY_STREAM;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.data.selection.ByType.getReferences;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ConstantSource;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.True;
import io.parsingdata.metal.expression.value.reference.Ref;
import io.parsingdata.metal.expression.value.reference.Ref.DefinitionRef;
import io.parsingdata.metal.expression.value.reference.Ref.NameRef;
import io.parsingdata.metal.expression.value.reference.Self;
import io.parsingdata.metal.token.Token;

public class EqualityTest {

    public static final Token LINKED_LIST_1 =
        seq("linkedlist",
            def("header", con(1), eq(con(0))),
            def("next", con(1)),
            opt(sub(token("linkedlist"), last(ref("next")))),
            def("footer", con(1), eq(con(1)))
        );

    public static final Token LINKED_LIST_COMPOSED_IDENTICAL =
        seq(LINKED_LIST_1,
            sub(LINKED_LIST_1, con(0)));

    @Test
    public void cycleWithIdenticalTokens() throws IOException {
        final Optional<ParseState> result = LINKED_LIST_COMPOSED_IDENTICAL.parse(env(stream(0, 0, 1)));
        assertTrue(result.isPresent());
        assertEquals(1, getAllValues(result.get().order, "header").size);
        assertEquals(2, getReferences(result.get().order).size);
    }

    public static final Token LINKED_LIST_2 =
        seq("linkedlist",
            def("header", con(1), eq(con(0))),
            def("next", con(1)),
            opt(sub(token("linkedlist"), last(ref("next")))),
            def("footer", con(1), eq(con(1)))
        );

    public static final Token LINKED_LIST_COMPOSED_EQUAL =
        seq(LINKED_LIST_1,
            sub(LINKED_LIST_2, con(0)));

    @Test
    public void cycleWithEqualTokens() throws IOException {
        final Optional<ParseState> result = LINKED_LIST_COMPOSED_EQUAL.parse(env(stream(0, 0, 1)));
        assertTrue(result.isPresent());
        assertEquals(1, getAllValues(result.get().order, "header").size);
        assertEquals(2, getReferences(result.get().order).size);
    }

    @Test
    public void singletons() {
        checkSingleton(new Self(), new Self());
        checkSingleton(new True(), new True());
    }

    private void checkSingleton(final Object object, final Object same) {
        assertFalse(object.equals(null));
        assertFalse(same.equals(null));
        assertTrue(object.equals(object));
        assertTrue(same.equals(same));
        assertTrue(object.equals(same));
        assertTrue(same.equals(object));
        assertFalse(object.equals(new Object() {}));
        assertEquals(object.hashCode(), same.hashCode());
    }

    @Test
    public void multiConstructorTypes() {
        final Encoding object = new Encoding();
        final Encoding same = new Encoding(Encoding.DEFAULT_SIGN, Encoding.DEFAULT_CHARSET, Encoding.DEFAULT_BYTE_ORDER);
        final List<Encoding> other = Arrays.asList(signed(), le(), new Encoding(Charset.forName("UTF-8")));
        assertFalse(object.equals(null));
        assertFalse(same.equals(null));
        assertTrue(object.equals(same));
        assertTrue(same.equals(object));
        final Object otherType = new Object() {};
        assertFalse(object.equals(otherType));
        assertFalse(same.equals(otherType));
        assertEquals(object.hashCode(), same.hashCode());
        for (Encoding e : other) {
            assertFalse(e.equals(null));
            assertTrue(e.equals(e));
            assertFalse(e.equals(object));
            assertFalse(object.equals(e));
            assertFalse(e.equals(same));
            assertFalse(same.equals(e));
            assertFalse(e.equals(otherType));
            assertNotEquals(object.hashCode(), e.hashCode());
            assertNotEquals(same.hashCode(), e.hashCode());
        }
    }

    @Test
    public void immutableList() {
        final ImmutableList<String> object = ImmutableList.create("a");
        assertFalse(object.equals(null));
        assertTrue(object.equals(ImmutableList.create("a")));
        assertTrue(object.equals(new ImmutableList<>().add("a")));
        assertFalse(object.equals("a"));
        assertTrue(object.add("b").equals(ImmutableList.create("a").add("b")));
        assertTrue(object.add("b").add("c").equals(ImmutableList.create("a").add("b").add("c")));
        assertFalse(object.add("b").equals(ImmutableList.create("a").add("c")));
        assertFalse(object.add("b").add("c").equals(ImmutableList.create("a").add("c").add("c")));
    }

    @Test
    public void parseGraph() {
        final ParseValue value = new ParseValue("a", any("a"), createFromBytes(new byte[]{1, 2}), enc());
        final ParseGraph object = createFromByteStream(DUMMY_STREAM).add(value).order;
        assertFalse(object.equals(null));
        assertFalse(object.equals("a"));
        final ParseState parseState = createFromByteStream(DUMMY_STREAM);
        assertNotEquals(parseState.addBranch(any("a")).add(value).add(value).closeBranch().addBranch(any("a")).order, parseState.addBranch(any("a")).closeBranch().addBranch(any("a")).order);
        assertNotEquals(parseState.addBranch(any("a")).order, parseState.addBranch(any("a")).closeBranch().order);
        assertNotEquals(parseState.addBranch(any("a")).order, parseState.addBranch(any("b")).order);
    }

    @Test
    public void stringRef() {
        final Ref object = new NameRef("name");
        assertFalse(object.equals(null));
        assertFalse(object.equals("name"));
        assertEquals(object, new NameRef("name"));
        assertNotEquals(object, new NameRef("otherName"));
        assertNotEquals(object, new DefinitionRef(any("name")));
        assertNotEquals(object, new NameRef("name", con(1)));
    }

    @Test
    public void definitionRef() {
        final Ref object = new DefinitionRef(any("name"));
        assertFalse(object.equals(null));
        assertFalse(object.equals("name"));
        assertEquals(object, new DefinitionRef(any("name")));
        assertNotEquals(object, new DefinitionRef(any("otherName")));
        assertNotEquals(object, new NameRef("name"));
        assertNotEquals(object, new DefinitionRef(any("name"), con(1)));
    }

    @Test
    public void slice() {
        final Slice object = Slice.createFromBytes(new byte[] { 0, 1, 2, 3 });
        assertFalse(object.equals(null));
        assertFalse(object.equals("name"));
        assertEquals(object, Slice.createFromBytes(new byte[] { 0, 1, 2, 3 }));
        assertNotEquals(object, Slice.createFromBytes(new byte[] { 0, 1, 2, 4 }));
        assertNotEquals(object, Slice.createFromSource(new ConstantSource(new byte[] { 0, 1, 2, 3 }), ONE, BigInteger.valueOf(2)).get());
        assertNotEquals(object, Slice.createFromSource(new ConstantSource(new byte[] { 0, 1, 2, 3 }), ZERO, BigInteger.valueOf(2)).get());
    }

}
