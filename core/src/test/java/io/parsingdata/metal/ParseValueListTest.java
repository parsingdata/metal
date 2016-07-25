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

import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.util.EncodingFactory.enc;

@RunWith(JUnit4.class)
public class ParseValueListTest {

    private final ParseValueList l1;
    private final ParseValueList l2;
    private final ParseValueList l3;
    private final ParseValueList l4;
    private final ParseValueList l5;
    private final ParseValue v1;
    private final ParseValue v2;
    private final ParseValue v3;
    private final ParseValue v4;
    private final ParseValue v5;

    public ParseValueListTest() {
        v1 = val('a');
        l1 = ParseValueList.create(v1);
        v2 = val('b');
        l2 = l1.add(v2);
        v3 = val('a');
        l3 = l2.add(v3);
        v4 = val('d');
        l4 = l3.add(v4);
        v5 = val('e');
        l5 = l4.add(v5);
    }

    @Test
    public void addList() {
        final ParseValueList l6 = l5.add(l5);
        Assert.assertEquals(v5, l6.head);
        Assert.assertEquals(v4, l6.tail.head);
        Assert.assertEquals(v3, l6.tail.tail.head);
        Assert.assertEquals(v2, l6.tail.tail.tail.head);
        Assert.assertEquals(v1, l6.tail.tail.tail.tail.head);
        Assert.assertEquals(v5, l6.tail.tail.tail.tail.tail.head);
        Assert.assertEquals(v4, l6.tail.tail.tail.tail.tail.tail.head);
        Assert.assertEquals(v3, l6.tail.tail.tail.tail.tail.tail.tail.head);
        Assert.assertEquals(v2, l6.tail.tail.tail.tail.tail.tail.tail.tail.head);
        Assert.assertEquals(v1, l6.tail.tail.tail.tail.tail.tail.tail.tail.tail.head);
        Assert.assertTrue(l6.tail.tail.tail.tail.tail.tail.tail.tail.tail.tail.isEmpty());
    }

    @Test
    public void traverse() {
        Assert.assertEquals(l5.head, v5);
        Assert.assertEquals(l5.tail, l4);
        Assert.assertEquals(l4.head, v4);
        Assert.assertEquals(l4.tail, l3);
        Assert.assertEquals(l3.head, v3);
        Assert.assertEquals(l3.tail, l2);
        Assert.assertEquals(l2.head, v2);
        Assert.assertEquals(l2.tail, l1);
        Assert.assertEquals(l1.head, v1);
        Assert.assertTrue(l1.tail.isEmpty());
    }

    @Test
    public void getSingleMatch() {
        Assert.assertEquals(l5.get("b"), v2);
    }

    @Test
    public void getSingleNoMatch() {
        Assert.assertNull(l5.get("f"));
    }

    @Test
    public void getMultiMultiMatch() {
        final ParseValueList res = l5.getAll("a");
        Assert.assertEquals(res.head, v3);
        Assert.assertEquals(res.tail.head, v1);
        Assert.assertTrue(res.tail.tail.isEmpty());
    }

    @Test
    public void getMultiSingleMatch() {
        final ParseValueList res = l5.getAll("d");
        Assert.assertEquals(res.head, v4);
        Assert.assertTrue(res.tail.isEmpty());
    }

    @Test
    public void getMultiNoMatch() {
        final ParseValueList res = l5.getAll("f");
        Assert.assertTrue(res.isEmpty());
    }

    @Test
    public void getScopedMatch() {
        final ParseValueList res = l5.getValuesSincePrefix(v3);
        Assert.assertEquals(res.head, v5);
        Assert.assertEquals(res.tail.head, v4);
        Assert.assertTrue(res.tail.tail.isEmpty());
    }

    @Test
    public void getScopedNoMatch() {
        final ParseValueList res = l5.getValuesSincePrefix(v5);
        Assert.assertTrue(res.isEmpty());
    }

    @Test
    public void reverse() {
        final ParseValueList rev = l5.reverse();
        Assert.assertEquals(rev.head, v1);
        Assert.assertEquals(rev.tail.head, v2);
        Assert.assertEquals(rev.tail.tail.head, v3);
        Assert.assertEquals(rev.tail.tail.tail.head, v4);
        Assert.assertEquals(rev.tail.tail.tail.tail.head, v5);
        Assert.assertTrue(rev.tail.tail.tail.tail.tail.isEmpty());
    }

    @Test
    public void reverseEmpty() {
        Assert.assertTrue(ParseValueList.EMPTY.reverse().isEmpty());
    }

    @Test
    public void size() {
        Assert.assertEquals(1, l1.size);
        Assert.assertEquals(5, l5.size);
    }

    @Test
    public void sizeEmpty() {
        Assert.assertEquals(0, ParseValueList.EMPTY.size);
    }

    private ParseValue val(final char c) {
        return new ParseValue(Character.toString(c), def(Character.toString(c), 0L), 0L, new byte[] { (byte) c }, enc());
    }

}
