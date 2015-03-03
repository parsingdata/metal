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
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        if (tail == null) { throw new IllegalArgumentException("Argument tail may not be null."); }
        this.head = head;
        this.tail = tail;
        size = tail.size + 1;
    }
    
    public static ParseGraphList create(final ParseGraph head) {
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        return EMPTY.add(head);
    }
    
    public ParseGraphList add(final ParseGraph head) {
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        return new ParseGraphList(head, this);
    }
    
    public ParseGraphList add(final ParseGraphList list) {
        if (list == null) { throw new IllegalArgumentException("Argument list may not be null."); }
        if (list.isEmpty()) { return this; }
        if (isEmpty()) { return list; }
        return add(list.tail).add(list.head);
    }
    
    public boolean isEmpty() {
        return size == 0;
    }

}
