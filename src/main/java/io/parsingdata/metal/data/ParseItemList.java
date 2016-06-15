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

import java.util.ArrayList;
import java.util.List;

public class ParseItemList {

    public final ParseItem head;
    public final ParseItemList tail;
    public final long size;

    public static final ParseItemList EMPTY = new ParseItemList();

    private ParseItemList() {
        head = null;
        tail = null;
        size = 0;
    }

    private ParseItemList(final ParseItem head, final ParseItemList tail) {
        this.head = checkNotNull(head, "head");
        this.tail = checkNotNull(tail, "tail");
        size = tail.size + 1;
    }

    public static ParseItemList create(final ParseItem head) {
        return EMPTY.add(checkNotNull(head, "head"));
    }

    public ParseItemList add(final ParseItem head) {
        return new ParseItemList(checkNotNull(head, "head"), this);
    }

    public ParseItemList add(final ParseItemList list) {
        checkNotNull(list, "list");
        if (list.isEmpty()) { return this; }
        if (isEmpty()) { return list; }
        return add(list.tail).add(list.head);
    }

    public ParseItem current() {
        return head;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseItem getFirst() {
        if (isEmpty()) { return null; }
        if (tail.isEmpty()) { return head; }
        return tail.getFirst();
    }

    public ParseItemList reverse() {
        if (isEmpty()) { return this; }
        return reverse(tail, create(head));
    }

    private ParseItemList reverse(final ParseItemList oldList, final ParseItemList newList) {
        if (oldList.isEmpty()) { return newList; }
        return reverse(oldList.tail, newList.add(oldList.head));
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head + tail.toString();
    }

    /**
     * Return all {@link ParseGraph}s as list by iterating recursively.
     * @return all {@link ParseGraph}s in <code>this</code> {@link ParseItemList}
     */
    public List<ParseGraph> asList() {
        ParseItemList parseItem = this;
        final List<ParseGraph> items = new ArrayList<>((int) parseItem.size);
        while (!parseItem.isEmpty()) {
            if (parseItem.head.isGraph()) {
                items.add(parseItem.head.asGraph());
            }
            parseItem = parseItem.tail;
        }
        return items;
    }
}
