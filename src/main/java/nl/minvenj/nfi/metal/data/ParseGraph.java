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
    public final boolean branched;
    public final long size;

    public static final ParseGraph EMPTY = new ParseGraph();

    private ParseGraph() {
        head = null;
        tail = null;
        branched = false;
        size = 0;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail, final boolean branched) {
        if (head == null) { throw new IllegalArgumentException("Argument head may not be null."); }
        if (tail == null) { throw new IllegalArgumentException("Argument tail may not be null."); }
        if (head.isValue() && branched) { throw new IllegalArgumentException("Argument branch cannot be true when head contains a ParseValue."); }
        this.head = head;
        this.tail = tail;
        this.branched = branched;
        size = tail.size + 1;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail) {
        this(head, tail, false);
    }

    public ParseGraph add(final ParseValue head) {
        if (branched) { return new ParseGraph(new ParseItem(this.head.getGraph().add(head)), tail, true); }
        return new ParseGraph(new ParseItem(head), this);
    }

    public ParseGraph addRef(final long ref) {
        if (branched) { return new ParseGraph(new ParseItem(this.head.getGraph().addRef(ref)), tail, true); }
        return new ParseGraph(new ParseItem(ref), this);
    }

    public ParseGraph addBranch() {
        if (branched) { return new ParseGraph(new ParseItem(this.head.getGraph().addBranch()), tail, true); }
        return new ParseGraph(new ParseItem(ParseGraph.EMPTY), this, true);
    }

    public ParseGraph closeBranch() {
        if (!branched) { throw new IllegalStateException("Cannot close branch that is not open."); }
        if (head.getGraph().branched) {
            return new ParseGraph(new ParseItem(head.getGraph().closeBranch()), tail, true);
        }
        return new ParseGraph(head, tail, false);
    }

    public ParseGraphList getGraphs() {
        return getNestedGraphs().add(this);
    }

    private ParseGraphList getNestedGraphs() {
        if (isEmpty()) { return ParseGraphList.EMPTY; }
        final ParseGraphList tailGraphs = tail.getNestedGraphs();
        if (head.isGraph()) { return tailGraphs.add(head.getGraph()).add(head.getGraph().getNestedGraphs()); }
        return tailGraphs;
    }

    public boolean containsValue() {
        if (isEmpty()) { return false; }
        return head.isValue() || tail.containsValue();
    }

    public ParseValue getLowestOffsetValue() {
        if (!containsValue()) { throw new IllegalStateException("Cannot determine lowest offset if graph does not contain a value."); }
        if (head.isValue()) { return tail.getLowestOffsetValue(head.getValue()); }
        return tail.getLowestOffsetValue();
    }

    private ParseValue getLowestOffsetValue(final ParseValue lowest) {
        if (!containsValue()) { return lowest; }
        if (head.isValue()) { return tail.getLowestOffsetValue(lowest.getOffset() < head.getValue().getOffset() ? lowest : head.getValue()); }
        return tail.getLowestOffsetValue(lowest);
    }

    public boolean hasGraphAtRef(final long ref) {
        return findRef(getGraphs(), ref) != null;
    }

    static ParseGraph findRef(final ParseGraphList graphs, final long ref) {
        if (graphs.isEmpty()) { return null; }
        final ParseGraph res = findRef(graphs.tail, ref);
        if (res != null) { return res; }
        if (graphs.head.containsValue() && graphs.head.getLowestOffsetValue().getOffset() == ref) {
            return graphs.head;
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseGraph reverse() {
        return reverse(this, EMPTY);
    }

    private ParseGraph reverse(final ParseGraph oldGraph, final ParseGraph newGraph) {
        if (oldGraph.isEmpty()) { return newGraph; }
        return reverse(oldGraph.tail, new ParseGraph(reverseItem(oldGraph.head), newGraph));
    }

    private ParseItem reverseItem(final ParseItem item) {
        return item.isGraph() ? new ParseItem(item.getGraph().reverse()) : item;
    }

    public ParseValueList flatten() {
        if (isEmpty()) { return ParseValueList.EMPTY; }
        return tail.flatten().add(head.isGraph() ? head.getGraph().flatten() : (head.isValue() ? ParseValueList.EMPTY.add(head.getValue()) : ParseValueList.EMPTY));
    }

    @Override
    public String toString() {
        return "ParseGraph(" + (head != null ? head.toString() : "null") + ", " + (tail != null ? tail.toString() : "null") + ", " + branched + ")";
    }

}
