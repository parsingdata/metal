/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import static io.parsingdata.metal.data.Slice.createFromSource;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.data.selection.ByType.getReferences;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ConstantSource;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ImmutablePair;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.True;
import io.parsingdata.metal.expression.value.SingleValueExpression;
import io.parsingdata.metal.expression.value.reference.Ref;
import io.parsingdata.metal.expression.value.reference.Ref.DefinitionRef;
import io.parsingdata.metal.expression.value.reference.Ref.NameRef;
import io.parsingdata.metal.expression.value.reference.Self;
import io.parsingdata.metal.token.Token;

@SuppressWarnings("PMD.EqualsNull") // Suppressed because this class explicitly checks for correct equals(null) behaviour.
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
    public void cycleWithIdenticalTokens() {
        final Optional<ParseState> result = LINKED_LIST_COMPOSED_IDENTICAL.parse(env(stream(0, 0, 1)));
        assertTrue(result.isPresent());
        ImmutableList<ParseValue> parseValues = getAllValues(result.get().order, "header");
        assertEquals(1, (long) parseValues.size());
        ImmutableList<Optional<ParseItem>> optionals = getReferences(result.get().order);
        assertEquals(2, (long) optionals.size());
    }

    @Test
    public void cycleWithEqualTokens() {
        final Optional<ParseState> result = LINKED_LIST_COMPOSED_EQUAL.parse(env(stream(0, 0, 1)));
        assertTrue(result.isPresent());
        ImmutableList<ParseValue> parseValues = getAllValues(result.get().order, "header");
        assertEquals(1, (long) parseValues.size());
        ImmutableList<Optional<ParseItem>> optionals = getReferences(result.get().order);
        assertEquals(2, (long) optionals.size());
    }

    @Test
    public void singletons() {
        checkSingleton(new Self(), new Self());
        checkSingleton(new True(), new True());
    }

    private void checkSingleton(final Object object, final Object same) {
        assertFalse(object.equals(null));
        assertFalse(same.equals(null));
        assertEquals(object, object);
        assertEquals(same, same);
        assertEquals(object, same);
        assertEquals(same, object);
        assertNotEquals(object, new Object() {});
        assertEquals(object.hashCode(), same.hashCode());
    }

    @Test
    public void multiConstructorTypes() {
        final Encoding object = enc();
        final Encoding same = new Encoding(Encoding.DEFAULT_SIGN, Encoding.DEFAULT_CHARSET, Encoding.DEFAULT_BYTE_ORDER);
        final List<Encoding> other = List.of(signed(), le(), new Encoding(Charset.forName("UTF-8")));
        assertFalse(object.equals(null));
        assertFalse(same.equals(null));
        assertEquals(object, same);
        assertEquals(same, object);
        final Object otherType = new Object() {};
        assertNotEquals(object, otherType);
        assertNotEquals(same, otherType);
        assertEquals(object.hashCode(), same.hashCode());
        for (Encoding e : other) {
            assertNotEquals(null, e);
            assertEquals(e, e);
            assertNotEquals(e, object);
            assertNotEquals(object, e);
            assertNotEquals(e, same);
            assertNotEquals(same, e);
            assertNotEquals(e, otherType);
            assertNotEquals(object.hashCode(), e.hashCode());
            assertNotEquals(same.hashCode(), e.hashCode());
        }
    }

    @Test
    public void immutableList() {
        final ImmutableList<Object> object = ImmutableList.create("a");
        assertFalse(object.equals(null));
        assertEquals(object, ImmutableList.create("a"));
        assertEquals(object, new ImmutableList<>().addHead("a"));
        assertNotEquals("a", object);
        assertEquals(object.addHead("b"), ImmutableList.create("a").addHead("b"));
        assertEquals(object.addHead("b").addHead("c"), ImmutableList.create("a").addHead("b").addHead("c"));
        assertNotEquals(object.addHead("b"), ImmutableList.create("a").addHead("c"));
        assertNotEquals(object.addHead("b").addHead("c"), ImmutableList.create("a").addHead("c").addHead("c"));
    }

    @Test
    public void immutablePair() {
        final ImmutablePair<String, BigInteger> object = new ImmutablePair<>("a", ONE);
        assertFalse(object.equals(null));
        assertEquals(object, new ImmutablePair<>("a", ONE));
        assertEquals(object.hashCode(), new ImmutablePair<>("a", ONE).hashCode());
        assertNotEquals("a", object);
        assertNotEquals(object.hashCode(), "a".hashCode());
        assertNotEquals(object, new ImmutablePair<>("b", ONE));
        assertNotEquals(object.hashCode(), new ImmutablePair<>("b", ONE).hashCode());
        assertNotEquals(object, new ImmutablePair<>("a", ZERO));
        assertNotEquals(object.hashCode(), new ImmutablePair<>("a", ZERO).hashCode());
    }

    @Test
    public void parseGraph() {
        final ParseValue value = new ParseValue("a", any("a"), createFromBytes(new byte[]{1, 2}), enc());
        final ParseGraph object = createFromByteStream(DUMMY_STREAM).add(value).order;
        assertFalse(object.equals(null));
        assertNotEquals("a", object);
        final ParseState parseState = createFromByteStream(DUMMY_STREAM);
        assertNotEquals(parseState.addBranch(any("a")).add(value).add(value).closeBranch(any("a")).addBranch(any("a")).order, parseState.addBranch(any("a")).closeBranch(any("a")).addBranch(any("a")).order);
        assertNotEquals(parseState.addBranch(any("a")).order, parseState.addBranch(any("a")).closeBranch(any("a")).order);
        assertNotEquals(parseState.addBranch(any("a")).order, parseState.addBranch(any("b")).order);
    }

    @Test
    public void stringRef() {
        final Ref<String> object = new NameRef("name");
        assertNotEquals(null, object);
        assertNotEquals("name", object);
        assertEquals(object, new NameRef("name"));
        assertNotEquals(object, new NameRef("name", "name"));
        assertNotEquals(object, new NameRef("name", "otherName"));
        assertNotEquals(object, new NameRef("otherName"));
        assertNotEquals(object, new DefinitionRef(any("name")));
        assertNotEquals(object, new NameRef(con(1), "name"));
        assertNotEquals(object, new NameRef(con(1), (SingleValueExpression) null, "name"));
    }

    @Test
    public void definitionRef() {
        final Ref<Token> object = new DefinitionRef(any("name"));
        assertNotEquals(null, object);
        assertNotEquals("name", object);
        assertEquals(object, new DefinitionRef(any("name")));
        assertNotEquals(object, new DefinitionRef(any("name"), any("name")));
        assertNotEquals(object, new DefinitionRef(any("name"), any("otherName")));
        assertNotEquals(object, new DefinitionRef(any("otherName")));
        assertNotEquals(object, new NameRef("name"));
        assertNotEquals(object, new DefinitionRef(con(1), any("name")));
        assertNotEquals(object, new DefinitionRef(con(1), (SingleValueExpression) null, any("name")));
    }

    @Test
    public void slice() {
        final Slice object = createFromBytes(new byte[] { 0, 1, 2, 3 });
        assertFalse(object.equals(null));
        assertNotEquals("name", object);
        assertEquals(object, createFromBytes(new byte[] { 0, 1, 2, 3 }));
        assertNotEquals(object, createFromBytes(new byte[] { 0, 1, 2, 4 }));
        assertNotEquals(object, createFromSource(new ConstantSource(new byte[] { 0, 1, 2, 3 }), ONE, BigInteger.valueOf(2)).get());
        assertNotEquals(object, createFromSource(new ConstantSource(new byte[] { 0, 1, 2, 3 }), ZERO, BigInteger.valueOf(2)).get());
    }

}
