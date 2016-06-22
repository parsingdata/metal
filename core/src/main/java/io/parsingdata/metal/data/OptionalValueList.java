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

import io.parsingdata.metal.expression.value.OptionalValue;

import static io.parsingdata.metal.Util.checkNotNull;

public class OptionalValueList {

    public final OptionalValue head;
    public final OptionalValueList tail;
    public final long size;

    public static final OptionalValueList EMPTY = new OptionalValueList();

    private OptionalValueList() {
        head = null;
        tail = null;
        size = 0;
    }

    private OptionalValueList(final OptionalValue head, final OptionalValueList tail) {
        this.head = checkNotNull(head, "head");
        this.tail = checkNotNull(tail, "tail");
        size = tail.size + 1;
    }

    public static OptionalValueList create(final OptionalValue head) {
        return EMPTY.add(head);
    }

    public OptionalValueList add(final OptionalValue head) {
        return new OptionalValueList(head, this);
    }

    public OptionalValueList add(final OptionalValueList list) {
        checkNotNull(list, "list");
        if (list.isEmpty()) { return this; }
        if (isEmpty()) { return list; }
        return add(list.tail).add(list.head);
    }

    public boolean isEmpty() { return size == 0; }

    public boolean containsValue() {
        if (isEmpty()) { return false; }
        return this.head.isPresent() || this.tail.containsValue();
    }

    public OptionalValueList reverse() {
        if (isEmpty()) { return this; }
        return reverse(tail, create(head));
    }

    private OptionalValueList reverse(final OptionalValueList oldList, final OptionalValueList newList) {
        if (oldList.isEmpty()) { return newList; }
        return reverse(oldList.tail, newList.add(oldList.head));
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head + tail.toString();
    }

}
