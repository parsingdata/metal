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

package io.parsingdata.metal.data.callback;

import static io.parsingdata.metal.Util.checkNotNull;

public class TokenCallbackList {

    public final TokenCallback head;
    public final TokenCallbackList tail;
    public final long size;

    public static final TokenCallbackList EMPTY = new TokenCallbackList();

    private TokenCallbackList() {
        head = null;
        tail = null;
        size = 0;
    }

    private TokenCallbackList(final TokenCallback head, final TokenCallbackList tail) {
        this.head = checkNotNull(head, "head");
        this.tail = checkNotNull(tail, "tail");
        size = tail.size + 1;
    }

    public static TokenCallbackList create(final TokenCallback head) {
        return EMPTY.add(checkNotNull(head, "head"));
    }

    public TokenCallbackList add(final TokenCallback head) {
        return new TokenCallbackList(checkNotNull(head, "head"), this);
    }

    public TokenCallbackList add(final TokenCallbackList list) {
        checkNotNull(list, "list");
        if (list.isEmpty()) { return this; }
        if (isEmpty()) { return list; }
        return add(list.tail).add(list.head);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head + tail.toString();
    }

}
