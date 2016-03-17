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

import java.io.IOException;

import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.token.Token;

public class ParseGraph implements ParseItem {

    public final ParseItem head;
    public final ParseGraph tail;
    public final boolean branched;
    public final Token definition;
    public final long size;

    public static final Token NONE = new Token(null) {
        @Override protected ParseResult parseImpl(String scope, Environment env, Encoding enc) throws IOException { throw new IllegalStateException("This placeholder may not be invoked."); }
        @Override public String toString() { return "None"; };
    };

    public static final ParseGraph EMPTY = new ParseGraph(NONE);

    private ParseGraph(final Token definition) {
        head = null;
        tail = null;
        branched = false;
        this.definition = checkNotNull(definition, "definition");
        size = 0;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail, final Token definition, final boolean branched) {
        this.head = checkNotNull(head, "head");
        if (head.isValue() && branched) { throw new IllegalArgumentException("Argument branch cannot be true when head contains a ParseValue."); }
        this.tail = checkNotNull(tail, "tail");
        this.branched = branched;
        this.definition = checkNotNull(definition, "definition");
        size = tail.size + 1;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail, final Token definition) {
        this(head, tail, definition, false);
    }

    public ParseGraph add(final ParseValue head) {
        if (branched) { return new ParseGraph(this.head.asGraph().add(head), tail, this.definition, true); }
        return new ParseGraph(head, this, this.definition);
    }

    public ParseGraph add(final ParseRef ref) {
        if (branched) { return new ParseGraph(this.head.asGraph().add(ref), tail, this.definition, true); }
        return new ParseGraph(ref, this, this.definition);
    }

    public ParseGraph addBranch(final Token definition) {
        if (branched) { return new ParseGraph(this.head.asGraph().addBranch(definition), tail, this.definition, true); }
        return new ParseGraph(new ParseGraph(definition), this, this.definition, true);
    }

    public ParseGraph closeBranch() {
        if (!branched) { throw new IllegalStateException("Cannot close branch that is not open."); }
        if (head.asGraph().branched) {
            return new ParseGraph(head.asGraph().closeBranch(), tail, this.definition, true);
        }
        return new ParseGraph(head, tail, this.definition, false);
    }

    public ParseGraphList getRefs() {
        return getRefs(this);
    }

    private ParseGraphList getRefs(final ParseGraph root) {
        if (isEmpty()) { return ParseGraphList.EMPTY; }
        if (head.isRef() && head.asRef().resolve(root) == null) { throw new IllegalStateException("A ref must point to an existing graph."); }
        return tail.getRefs(root).add(head.isGraph() ? head.asGraph().getRefs(root) : (head.isRef() ? ParseGraphList.EMPTY.add(head.asRef().resolve(root)) : ParseGraphList.EMPTY));
    }

    public ParseGraphList getGraphs() {
        return getNestedGraphs().add(this);
    }

    private ParseGraphList getNestedGraphs() {
        if (isEmpty()) { return ParseGraphList.EMPTY; }
        final ParseGraphList tailGraphs = tail.getNestedGraphs();
        if (head.isGraph()) { return tailGraphs.add(head.asGraph()).add(head.asGraph().getNestedGraphs()); }
        return tailGraphs;
    }

    public boolean containsValue() {
        if (isEmpty()) { return false; }
        return head.isValue() || tail.containsValue();
    }

    public ParseValue getLowestOffsetValue() {
        if (!containsValue()) { throw new IllegalStateException("Cannot determine lowest offset if graph does not contain a value."); }
        if (head.isValue()) { return tail.getLowestOffsetValue(head.asValue()); }
        return tail.getLowestOffsetValue();
    }

    private ParseValue getLowestOffsetValue(final ParseValue lowest) {
        if (!containsValue()) { return lowest; }
        if (head.isValue()) { return tail.getLowestOffsetValue(lowest.getOffset() < (head.asValue()).getOffset() ? lowest : head.asValue()); }
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
        }
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseGraph reverse() {
        return reverse(this, EMPTY);
    }

    /**
     * @param name Name of the value
     * @return The first value (bottom-up) with the provided name in this graph
     */
    public ParseValue get(final String name) {
        if (isEmpty()) { return null; }
        if (head.isValue() && head.asValue().matches(name)) { return head.asValue(); }
        if (head.isGraph()) {
            final ParseValue val = head.asGraph().get(name);
            if (val != null) { return val; }
        }
        return tail.get(name);
    }

    public ParseItem get(final Token definition) {
        if (definition == null) { throw new IllegalArgumentException("Argument definition may not be null."); }
        if (this.definition == definition) { return this; }
        if (isEmpty()) { return null; }
        if (head.isValue() && head.asValue().definition == definition) { return head; }
        if (head.isGraph()) {
            final ParseItem item = head.asGraph().get(definition);
            if (item != null) { return item; }
        }
        return tail.get(definition);
    }

    /**
     * @return The first value (bottom-up) in this graph
     */
    public ParseValue current() {
        if (isEmpty()) { return null; }
        if (head.isValue()) { return head.asValue(); }
        if (head.isGraph()) {
            final ParseValue val = head.asGraph().current();
            if (val != null) { return val; }
        }
        return tail.current(); // Ignore current if it's a reference (or an empty graph)
    }

    /**
     * @param name Name of the value
     * @return All values with the provided name in this graph
     */
    public ParseValueList getAll(final String name) {
        return getAll(name, ParseValueList.EMPTY);
    }

    private ParseValueList getAll(final String name, final ParseValueList result) {
        if (isEmpty()) { return result; }
        final ParseValueList tailResults = tail.getAll(name, result);
        if (head.isValue() && head.asValue().matches(name)) { return tailResults.add(head.asValue()); }
        if (head.isGraph()) { return tailResults.add(head.asGraph().getAll(name, result)); }
        return tailResults;
    }

    private ParseGraph reverse(final ParseGraph oldGraph, final ParseGraph newGraph) {
        if (oldGraph.isEmpty()) { return newGraph; }
        return reverse(oldGraph.tail, new ParseGraph(reverseItem(oldGraph.head), newGraph, definition));
    }

    private ParseItem reverseItem(final ParseItem item) {
        return item.isGraph() ? item.asGraph().reverse() : item;
    }

    /**
     * @param lastHead The first item (bottom-up) to be excluded
     * @return The subgraph of this graph starting past (bottom-up) the provided lastHead
     */
    public ParseGraph getGraphAfter(final ParseItem lastHead) {
        return getGraphAfter(lastHead, EMPTY);
    }

    private ParseGraph getGraphAfter(final ParseItem lastHead, final ParseGraph result) {
        if (isEmpty()) { return EMPTY; }
        if (head == lastHead) { return result; }
        return new ParseGraph(head, tail.getGraphAfter(lastHead, result), definition);
    }

    @Override public boolean isValue() { return false; }
    @Override public boolean isGraph() { return true; }
    @Override public boolean isRef() { return false; }
    @Override public ParseValue asValue() { throw new UnsupportedOperationException("Cannot convert ParseGraph to ParseValue."); }
    @Override public ParseGraph asGraph() { return this; }
    @Override public ParseRef asRef() { throw new UnsupportedOperationException("Cannot convert ParseGraph to ParseRef."); }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        return "ParseGraph(" + (head != null ? head.toString() : "null") + ", " + (tail != null ? tail.toString() : "null") + ", " + branched + ")";
    }

}
