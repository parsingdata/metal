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

public class ParseList {

    public final ParseValue head;
    public final ParseList tail;
    public final long size;

    public static final ParseList EMPTY = new ParseList();

    private ParseList() {
        head = null;
        tail = null;
        size = 0;
    }

    private ParseList(final ParseValue head, final ParseList tail) {
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        if (tail == null) { throw new IllegalArgumentException("Argument tail may not be null."); }
        this.head = head;
        this.tail = tail;
        size = tail.size + 1;
    }

    public static ParseList create(final ParseValue head) {
        return EMPTY.add(head);
    }

    public ParseList add(final ParseValue head) {
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        return new ParseList(head, this);
    }

    public ParseList add(final ParseList list) {
        if (list.isEmpty()) { return this; }
        if (isEmpty()) { return list; }
        return add(list.tail).add(list.head);
    }

    public ParseValue get(final String name) {
        if (isEmpty()) { return null; }
        if (head.matches(name)) {
            return head;
        } else {
            return tail.get(name);
        }
    }

    public ParseList getAll(final String name) {
        if (isEmpty()) { return this; }
        final ParseList t = tail.getAll(name);
        if (head.matches(name)) { return t.add(head); }
        else { return t; }
    }

    public ParseList getValuesSincePrefix(final ParseValue prefix) {
        if (isEmpty()) { return this; }
        if (head == prefix) { return EMPTY; }
        final ParseList t = tail.getValuesSincePrefix(prefix);
        return t.add(head);
    }

    public ParseValue current() {
        return head;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseValue getFirst() {
        if (isEmpty()) { return null; }
        if (tail.isEmpty()) { return head; }
        return tail.getFirst();
    }

    public boolean containsOffset(final long offset) {
        if (isEmpty()) { return false; }
        if (head.getOffset() == offset) { return true; }
        return tail.containsOffset(offset);
    }

    public ParseList reverse() {
        if (isEmpty()) { return this; }
        return reverse(tail, create(head));
    }

    private ParseList reverse(final ParseList oldList, final ParseList newList) {
        if (oldList.isEmpty()) { return newList; }
        return reverse(oldList.tail, newList.add(oldList.head));
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head + tail.toString();
    }

}
