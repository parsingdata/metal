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

import nl.minvenj.nfi.ddrx.data.ValueList;
import nl.minvenj.nfi.ddrx.expression.value.Value;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValueListTest {
    
    private final ValueList l1;
    private final ValueList l2;
    private final ValueList l3;
    private final ValueList l4;
    private final ValueList l5;
    private final Value v1;
    private final Value v2;
    private final Value v3;
    private final Value v4;
    private final Value v5;

    public ValueListTest() {
        v1 = val("s1", 'a');
        l1 = new ValueList(v1);
        v2 = val("s1", 'b');
        l2 = new ValueList(v2, l1);
        v3 = val("s2", 'a');
        l3 = new ValueList(v3, l2);
        v4 = val("s1", 'd');
        l4 = new ValueList(v4, l3);
        v5 = val("s1", 'e');
        l5 = new ValueList(v5, l4);
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
        Assert.assertEquals(l1.tail, null);
    }
    
    @Test
    public void getSingleMatch() {
        Assert.assertEquals(l5.get("b").get(), v2);
    }
    
    @Test
    public void getSingleNoMatch() {
        Assert.assertFalse(l5.get("f").isPresent());
    }
    
    @Test
    public void getMultiMultiMatch() {
        final ValueList res = l5.getAll("a");
        Assert.assertEquals(res.head, v3);
        Assert.assertEquals(res.tail.head, v1);
        Assert.assertNull(res.tail.tail);
    }
    
    @Test
    public void getMultiSingleMatch() {
        final ValueList res = l5.getAll("d");
        Assert.assertEquals(res.head, v4);
        Assert.assertNull(res.tail);
    }
    
    @Test
    public void getMultiNoMatch() {
        final ValueList res = l5.getAll("f");
        Assert.assertNull(res.head);
        Assert.assertNull(res.tail);
    }
    
    @Test
    public void getScopedMatch() {
        final ValueList res = l5.getValuesSincePrefix(v3);
        Assert.assertEquals(res.head, v5);
        Assert.assertEquals(res.tail.head, v4);
        Assert.assertNull(res.tail.tail);
    }
    
    @Test
    public void getScopedNoMatch() {
        final ValueList res = l5.getValuesSincePrefix(v5);
        Assert.assertNull(res.head);
        Assert.assertNull(res.tail);
    }
    
    @Test
    public void reverse() {
        final ValueList rev = l5.reverse();
        Assert.assertEquals(rev.head, v1);
        Assert.assertEquals(rev.tail.head, v2);
        Assert.assertEquals(rev.tail.tail.head, v3);
        Assert.assertEquals(rev.tail.tail.tail.head, v4);
        Assert.assertEquals(rev.tail.tail.tail.tail.head, v5);
        Assert.assertNull(rev.tail.tail.tail.tail.tail);
    }
    
    @Test
    public void reverseEmpty() {
        Assert.assertTrue(new ValueList().reverse().isEmpty());
    }
    
    private Value val(final String s, final char c) {
        return new Value(s, Character.toString(c), 0L, new byte[] { (byte)c }, null);
    }

}
