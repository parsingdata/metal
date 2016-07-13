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

package io.parsingdata.metal.data;

import static io.parsingdata.metal.Util.checkNotNull;

public class ParseValueList {

    public final ParseValue head;
    public final ParseValueList tail;
    public final long size;

    public static final ParseValueList EMPTY = new ParseValueList();

    private ParseValueList() {
        head = null;
        tail = null;
        size = 0;
    }

    private ParseValueList(final ParseValue head, final ParseValueList tail) {
        this.head = checkNotNull(head, "head");
        this.tail = checkNotNull(tail, "tail");
        size = tail.size + 1;
    }

    public static ParseValueList create(final ParseValue head) {
        return EMPTY.add(checkNotNull(head, "head"));
    }

    public ParseValueList add(final ParseValue head) {
        return new ParseValueList(checkNotNull(head, "head"), this);
    }

    public ParseValueList add(final ParseValueList list) {
        checkNotNull(list, "list");
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

    public ParseValueList getAll(final String name) {
        if (isEmpty()) { return this; }
        final ParseValueList t = tail.getAll(name);
        if (head.matches(name)) { return t.add(head); }
        else { return t; }
    }

    public ParseValueList getValuesSincePrefix(final ParseValue prefix) {
        if (isEmpty()) { return this; }
        if (head == prefix) { return EMPTY; }
        final ParseValueList t = tail.getValuesSincePrefix(prefix);
        return t.add(head);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseValueList reverse() {
        if (isEmpty()) { return this; }
        return reverse(tail, create(head));
    }

    private ParseValueList reverse(final ParseValueList oldList, final ParseValueList newList) {
        if (oldList.isEmpty()) { return newList; }
        return reverse(oldList.tail, newList.add(oldList.head));
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head + tail.toString();
    }

}
