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

package nl.minvenj.nfi.metal.data;

import nl.minvenj.nfi.metal.expression.value.ParsedValue;

public class ParsedValueList {

    public final ParsedValue head;
    public final ParsedValueList tail;
    public final long size;

    public static final ParsedValueList EMPTY = new ParsedValueList();

    private ParsedValueList() {
        head = null;
        tail = null;
        size = 0;
    }

    private ParsedValueList(final ParsedValue head, final ParsedValueList tail) {
        assert head != null : "Argument head may not be null";
        assert tail != null : "Argument tail may not be null";
        this.head = head;
        this.tail = tail;
        size = tail.size + 1;
    }

    public static ParsedValueList create(final ParsedValue head) {
        return EMPTY.add(head);
    }

    public ParsedValueList add(final ParsedValue head) {
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        return new ParsedValueList(head, this);
    }

    public ParsedValue get(final String name) {
        if (isEmpty()) { return null; }
        if (head.matches(name)) {
            return head;
        } else {
            return tail.get(name);
        }
    }

    public ParsedValueList getAll(final String name) {
        if (isEmpty()) { return this; }
        final ParsedValueList t = tail.getAll(name);
        if (head.matches(name)) { return t.add(head); }
        else { return t; }
    }

    public ParsedValueList getValuesSincePrefix(final ParsedValue prefix) {
        if (isEmpty()) { return this; }
        if (head == prefix) { return EMPTY; }
        final ParsedValueList t = tail.getValuesSincePrefix(prefix);
        return t.add(head);
    }

    public ParsedValue current() {
        return head;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParsedValue getFirst() {
        if (isEmpty()) { return null; }
        if (tail.isEmpty()) { return head; }
        return tail.getFirst();
    }

    public boolean containsOffset(final long offset) {
        if (isEmpty()) { return false; }
        if (head.getOffset() == offset) { return true; }
        return tail.containsOffset(offset);
    }

    public ParsedValueList reverse() {
        if (isEmpty()) { return this; }
        return reverse(tail, create(head));
    }

    private ParsedValueList reverse(final ParsedValueList oldList, final ParsedValueList newList) {
        if (oldList.isEmpty()) { return newList; }
        return reverse(oldList.tail, newList.add(oldList.head));
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head + tail.toString();
    }

}
