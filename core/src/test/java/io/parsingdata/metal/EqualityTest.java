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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
import static io.parsingdata.metal.Util.createFromBytes;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.data.selection.ByPredicate.NO_LIMIT;
import static io.parsingdata.metal.data.selection.ByType.getReferences;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseValue;
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
        final Optional<Environment> result = LINKED_LIST_COMPOSED_IDENTICAL.parse(stream(0, 0, 1), enc());
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
        final Optional<Environment> result = LINKED_LIST_COMPOSED_EQUAL.parse(stream(0, 0, 1), enc());
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
        final Encoding same = new Encoding(Encoding.DEFAULT_SIGNED, Encoding.DEFAULT_CHARSET, Encoding.DEFAULT_BYTE_ORDER);
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
        final ParseGraph object = ParseGraph.EMPTY.add(value);
        assertFalse(object.equals(null));
        assertFalse(object.equals("a"));
        final Environment environment = new Environment((offset, data) -> 0);
        assertNotEquals(environment.addBranch(any("a")).add(value).add(value).closeBranch().addBranch(any("a")).order, environment.addBranch(any("a")).closeBranch().addBranch(any("a")).order);
        assertNotEquals(environment.addBranch(any("a")).order, environment.addBranch(any("a")).closeBranch().order);
        assertNotEquals(environment.addBranch(any("a")).order, environment.addBranch(any("b")).order);
    }

    @Test
    public void stringRef() {
        final Ref object = new NameRef("name");
        assertFalse(object.equals(null));
        assertFalse(object.equals("name"));
        assertEquals(object, new NameRef("name"));
        assertEquals(object, new NameRef("name", NO_LIMIT));
        assertNotEquals(object, new NameRef("otherName"));
        assertNotEquals(object, new DefinitionRef(any("name")));
        assertNotEquals(object, new NameRef("name", 1));
    }

    @Test
    public void definitionRef() {
        final Ref object = new DefinitionRef(any("name"));
        assertFalse(object.equals(null));
        assertFalse(object.equals("name"));
        assertEquals(object, new DefinitionRef(any("name")));
        assertEquals(object, new DefinitionRef(any("name"), NO_LIMIT));
        assertNotEquals(object, new DefinitionRef(any("otherName")));
        assertNotEquals(object, new NameRef("name"));
        assertNotEquals(object, new DefinitionRef(any("name"), 1));
    }

}
