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

public class ParseGraph implements ParseItem {

    public final ParseItem head;
    public final ParseGraph tail;
    public final long size;

    public static final ParseGraph EMPTY = new ParseGraph();

    private ParseGraph() {
        head = null;
        tail = null;
        size = 0;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail) {
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        if (tail == null) { throw new IllegalArgumentException("Argument tail may not be null."); }
        this.head = head;
        this.tail = tail;
        size = tail.size + 1;
    }

    public static ParseGraph create(final ParseItem head) {
        return EMPTY.add(head);
    }

    public ParseGraph add(final ParseItem head) {
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        return new ParseGraph(head, this);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseList flatten() {
        if (isEmpty()) { return ParseList.EMPTY; }
        return tail.flatten().add(head instanceof ParseGraph ? ((ParseGraph) head).flatten() : ParseList.EMPTY.add((ParseValue) head));
    }

/*    public ParseValue get(final String name) {
        if (isEmpty()) { return null; }
        if (head instanceof ParseGraph) {
            final ParseValue val = ((ParseGraph) head).get(name);
            if (val != null) { return val; }
        } else {
            if (((ParseValue) head).matches(name)) { return (ParseValue) head; }
        }
        return tail.get(name);
    }

*/}
