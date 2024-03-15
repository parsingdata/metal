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

package io.parsingdata.metal.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.data.Selection.reverse;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.data.selection.ByName.get;
import static io.parsingdata.metal.data.selection.ByName.getAll;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import org.junit.jupiter.api.Test;

public class ImmutableListHeadTailTest {

    private final ImmutableList<ParseValue> l1;
    private final ImmutableList<ParseValue> l2;
    private final ImmutableList<ParseValue> l3;
    private final ImmutableList<ParseValue> l4;
    private final ImmutableList<ParseValue> l5;
    private final ParseValue v1;
    private final ParseValue v2;
    private final ParseValue v3;
    private final ParseValue v4;
    private final ParseValue v5;

    public ImmutableListHeadTailTest() {
        v1 = val('a');
        l1 = ImmutableList.create(v1);
        v2 = val('b');
        l2 = l1.addHead(v2);
        v3 = val('a');
        l3 = l2.addHead(v3);
        v4 = val('d');
        l4 = l3.addHead(v4);
        v5 = val('e');
        l5 = l4.addHead(v5);
    }

    @Test
    public void addList() {
        final ImmutableList<ParseValue> l6 = l5.addList(l5);
        assertEquals(v5, l6.head());
        assertEquals(v4, l6.tail().head());
        assertEquals(v3, l6.tail().tail().head());
        assertEquals(v2, l6.tail().tail().tail().head());
        assertEquals(v1, l6.tail().tail().tail().tail().head());
        assertEquals(v5, l6.tail().tail().tail().tail().tail().head());
        assertEquals(v4, l6.tail().tail().tail().tail().tail().tail().head());
        assertEquals(v3, l6.tail().tail().tail().tail().tail().tail().tail().head());
        assertEquals(v2, l6.tail().tail().tail().tail().tail().tail().tail().tail().head());
        assertEquals(v1, l6.tail().tail().tail().tail().tail().tail().tail().tail().tail().head());
        assertTrue(l6.tail().tail().tail().tail().tail().tail().tail().tail().tail().tail().isEmpty());
    }

    @Test
    public void traverse() {
        assertEquals(l5.head(), v5);
        assertEquals(l5.tail(), l4);
        assertEquals(l4.head(), v4);
        assertEquals(l4.tail(), l3);
        assertEquals(l3.head(), v3);
        assertEquals(l3.tail(), l2);
        assertEquals(l2.head(), v2);
        assertEquals(l2.tail(), l1);
        assertEquals(l1.head(), v1);
        assertTrue(l1.tail().isEmpty());
    }

    @Test
    public void getSingleMatch() {
        assertEquals(get(l5, "b"), v2);
    }

    @Test
    public void getSingleNoMatch() {
        assertNull(get(l5, "f"));
    }

    @Test
    public void getMultiMultiMatch() {
        final ImmutableList<ParseValue> res = getAll(l5, "a");
        assertEquals(res.head(), v3);
        assertEquals(res.tail().head(), v1);
        assertTrue(res.tail().tail().isEmpty());
    }

    @Test
    public void getMultiSingleMatch() {
        final ImmutableList<ParseValue> res = getAll(l5, "d");
        assertEquals(res.head(), v4);
        assertTrue(res.tail().isEmpty());
    }

    @Test
    public void getMultiNoMatch() {
        final ImmutableList<ParseValue> res = getAll(l5, "f");
        assertTrue(res.isEmpty());
    }

    @Test
    public void reverseRegular() {
        final ImmutableList<ParseValue> rev = reverse(l5);
        assertEquals(rev.head(), v1);
        assertEquals(rev.tail().head(), v2);
        assertEquals(rev.tail().tail().head(), v3);
        assertEquals(rev.tail().tail().tail().head(), v4);
        assertEquals(rev.tail().tail().tail().tail().head(), v5);
        assertTrue(rev.tail().tail().tail().tail().tail().isEmpty());
    }

    @Test
    public void reverseEmpty() {
        assertTrue(reverse(new ImmutableList<ParseValue>()).isEmpty());
    }

    @Test
    public void size() {
        assertEquals(1, (long) l1.size());
        assertEquals(5, (long) l5.size());
    }

    @Test
    public void sizeEmpty() {
        ImmutableList<ParseValue> parseValues = new ImmutableList<ParseValue>();
        assertEquals(0, (long) parseValues.size());
    }

    private ParseValue val(final char c) {
        return new ParseValue(Character.toString(c), def(Character.toString(c), 0L), createFromBytes(new byte[] { (byte) c }), enc());
    }

}
