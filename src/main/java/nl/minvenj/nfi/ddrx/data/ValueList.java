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

package nl.minvenj.nfi.ddrx.data;

import nl.minvenj.nfi.ddrx.expression.value.OptionalValue;
import nl.minvenj.nfi.ddrx.expression.value.Value;

public class ValueList {
    
    public final Value head;
    public final ValueList tail;
    
    public ValueList() {
        this(null, null);
    }
    
    public ValueList(final Value head) {
        this(head, null);
    }
    
    public ValueList(final Value head, final ValueList tail){
        this.head = head;
        if (tail != null && tail.isEmpty()) {
            this.tail = null;
        } else {
            this.tail = tail;
        }
    }
    
    public OptionalValue get(final String name) {
        if (head != null && head.matches(name)) {
            return OptionalValue.of(head);
        } else if (tail != null) {
            return tail.get(name);
        } else {
            return OptionalValue.empty();
        }
    }
    
    public ValueList getAll(final String name) {
        final ValueList t = tail != null ? tail.getAll(name) : null;
        if (head != null && head.matches(name)) {
            return new ValueList(head, t != null && t.head != null ? t : null);
        } else {
            return t != null ? t : new ValueList();
        }
    }
    
    public ValueList getValuesSincePrefix(final Value prefix) {
        if (head != null && head != prefix) {
            final ValueList t = tail != null ? tail.getValuesSincePrefix(prefix) : null;
            return new ValueList(head, t != null && t.head != null ? t : null);
        } else {
            return new ValueList();
        }
    }
    
    public OptionalValue current() {
        return OptionalValue.of(head);
    }
    
    public boolean isEmpty() {
        return head == null && tail == null;
    }
    
    public OptionalValue getFirst() {
        if (head == null) { return OptionalValue.empty(); }
        if (tail == null) { return OptionalValue.of(head); }
        return tail.getFirst();
    }
    
    public ValueList reverse() {
        if (isEmpty()) { return this; }
        return reverse(tail, new ValueList(head, null));
    }
    
    private ValueList reverse(final ValueList head, final ValueList tail) {
        if (head == null) { return tail; }
        return reverse(head.tail, new ValueList(head.head, tail));
    }
    
    @Override
    public String toString() {
        return (head != null ? ">" + head : "") + (tail != null ? tail.toString() : "");
    }
    
}
