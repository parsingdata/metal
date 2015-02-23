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

public class ParseGraph {

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

    public static ParseGraph create(final ParseValue head) {
        return new ParseGraph(new ParseItem(head), EMPTY);
    }

    public ParseGraph add(final ParseValue head) {
        if (this.head.isGraph() && this.head.isOpen()) {
            return new ParseGraph(new ParseItem(new ParseGraph(new ParseItem(head), this.head.getGraph()), true), this);
        }
        return new ParseGraph(new ParseItem(head), this);
    }

    public ParseGraph addBranch() {
        if (this.head.isGraph() && this.head.isOpen()) {
            return new ParseGraph(new ParseItem(this.head.getGraph().addBranch(), true), this);
        }
        return new ParseGraph(new ParseItem(EMPTY, true), this);
    }

    public ParseGraph endBranch() {
        if (this.head.isGraph() && this.head.isOpen() && !this.head.getGraph().isEmpty()) {
            return new ParseGraph(new ParseItem(head.getGraph().endBranch(), true), this);
        }
        return this;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseList flatten() {
        if (isEmpty()) { return ParseList.EMPTY; }
        return tail.flatten().add(head.isGraph() ? head.getGraph().flatten() : ParseList.EMPTY.add(head.getValue()));
    }

    @Override
    public String toString() {
        return "ParseGraph(" + (head != null ? head.toString() : "null") + ", " + (tail != null ? tail.toString() : "null") + ")";
    }

}
