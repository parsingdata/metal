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

import nl.minvenj.nfi.ddrx.expression.value.ParsedValue;

public class ParsedValueList {

    public final ParsedValue head;
    public final ParsedValueList tail;

    public ParsedValueList() {
        this(null, null);
    }

    public ParsedValueList(final ParsedValue head) {
        this(head, null);
    }

    public ParsedValueList(final ParsedValue head, final ParsedValueList tail){
        this.head = head;
        if (tail != null && tail.isEmpty()) {
            this.tail = null;
        } else {
            this.tail = tail;
        }
    }

    public ParsedValue get(final String name) {
        if (head != null && head.matches(name)) {
            return head;
        } else if (tail != null) {
            return tail.get(name);
        } else {
            return null;
        }
    }

    public ParsedValueList getAll(final String name) {
        final ParsedValueList t = tail != null ? tail.getAll(name) : null;
        if (head != null && head.matches(name)) {
            return new ParsedValueList(head, t != null && t.head != null ? t : null);
        } else {
            return t != null ? t : new ParsedValueList();
        }
    }

    public ParsedValueList getValuesSincePrefix(final ParsedValue prefix) {
        if (head != null && head != prefix) {
            final ParsedValueList t = tail != null ? tail.getValuesSincePrefix(prefix) : null;
            return new ParsedValueList(head, t != null && t.head != null ? t : null);
        } else {
            return new ParsedValueList();
        }
    }

    public ParsedValue current() {
        return head;
    }

    public boolean isEmpty() {
        return head == null && tail == null;
    }

    public ParsedValue getFirst() {
        if (head == null) { return null; }
        if (tail == null) { return head; }
        return tail.getFirst();
    }

    public boolean containsOffset(final long offset) {
        if (head == null) { return false; }
        if (head.getOffset() == offset) { return true; }
        if (tail == null) { return false; }
        return tail.containsOffset(offset);
    }

    public ParsedValueList reverse() {
        if (isEmpty()) { return this; }
        return reverse(tail, new ParsedValueList(head, null));
    }

    private ParsedValueList reverse(final ParsedValueList head, final ParsedValueList tail) {
        if (head == null) { return tail; }
        return reverse(head.tail, new ParsedValueList(head.head, tail));
    }

    @Override
    public String toString() {
        return (head != null ? ">" + head : "") + (tail != null ? tail.toString() : "");
    }

}
