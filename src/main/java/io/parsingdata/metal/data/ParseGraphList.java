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

import static nl.minvenj.nfi.metal.Util.checkNotNull;

public class ParseGraphList {

    public final ParseGraph head;
    public final ParseGraphList tail;
    public final long size;

    public static final ParseGraphList EMPTY = new ParseGraphList();

    private ParseGraphList() {
        head = null;
        tail = null;
        size = 0;
    }

    private ParseGraphList(final ParseGraph head, final ParseGraphList tail) {
        this.head = checkNotNull(head, "head");
        this.tail = checkNotNull(tail, "tail");
        size = tail.size + 1;
    }

    public static ParseGraphList create(final ParseGraph head) {
        return EMPTY.add(checkNotNull(head, "head"));
    }

    public ParseGraphList add(final ParseGraph head) {
        return new ParseGraphList(checkNotNull(head, "head"), this);
    }

    public ParseGraphList add(final ParseGraphList list) {
        checkNotNull(list, "list");
        if (list.isEmpty()) { return this; }
        if (isEmpty()) { return list; }
        return add(list.tail).add(list.head);
    }

    public boolean isEmpty() {
        return size == 0;
    }

}
