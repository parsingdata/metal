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

import java.io.IOException;

import io.parsingdata.metal.data.selection.ByItem;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.data.selection.ByOffset;
import io.parsingdata.metal.data.selection.ByToken;
import io.parsingdata.metal.data.selection.ByType;
import io.parsingdata.metal.data.transformation.Reversal;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

public class ParseGraph implements ParseItem {

    public final ParseItem head;
    public final ParseGraph tail;
    public final boolean branched;
    public final Token definition;
    public final long size;
    public final long sequenceId;

    public static final Token NONE = new Token(null) {
        @Override protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException { throw new IllegalStateException("This placeholder may not be invoked."); }
        @Override public String toString() { return "None"; };
    };

    public static final ParseGraph EMPTY = new ParseGraph(NONE, -1);

    private ParseGraph(final Token definition, final long sequenceId) {
        head = null;
        tail = null;
        branched = false;
        this.definition = checkNotNull(definition, "definition");
        size = 0;
        this.sequenceId = sequenceId;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail, final Token definition, final boolean branched, final long sequenceId) {
        this.head = checkNotNull(head, "head");
        if (head.isValue() && branched) { throw new IllegalArgumentException("Argument branch cannot be true when head contains a ParseValue."); }
        this.tail = checkNotNull(tail, "tail");
        this.branched = branched;
        this.definition = checkNotNull(definition, "definition");
        size = tail.size + 1;
        this.sequenceId = sequenceId;
    }

    // TODO see ByItem, this constructor used to be private
    public ParseGraph(final ParseItem head, final ParseGraph tail, final Token definition, final long sequenceId) {
        this(head, tail, definition, false, sequenceId);
    }

    public ParseGraph add(final ParseValue head) {
        final long sequenceId = this.head.getSequenceId() + 1;
        if (branched) {
            return new ParseGraph(this.head.asGraph().add(head), tail, this.definition, true, sequenceId);
        }
        return new ParseGraph(head, this, this.definition, sequenceId);
    }

    public ParseGraph add(final ParseRef ref) {
        final long sequenceId = this.head.getSequenceId() + 1;
        if (branched) {
            return new ParseGraph(this.head.asGraph().add(ref), tail, this.definition, true, sequenceId);
        }
        return new ParseGraph(ref, this, this.definition, sequenceId);
    }

    public ParseGraph addBranch(final Token definition) {
        final long sequenceId = this.head.getSequenceId() + 1;
        if (branched) {
            return new ParseGraph(this.head.asGraph().addBranch(definition), tail, this.definition, true, sequenceId);
        }
        return new ParseGraph(new ParseGraph(definition, sequenceId + 1), this, this.definition, true, sequenceId);
    }

    public ParseGraph closeBranch() {
        if (!branched) { throw new IllegalStateException("Cannot close branch that is not open."); }
        final long sequenceId = head.getSequenceId() + 1;
        if (head.asGraph().branched) {
            return new ParseGraph(head.asGraph().closeBranch(), tail, this.definition, true, sequenceId);
        }
        return new ParseGraph(head, tail, this.definition, false, sequenceId);
    }

    public ParseGraphList getRefs() {
        return ByType.getRefs(this);
    }

    public ParseGraphList getGraphs() {
        return ByType.getGraphs(this);
    }

    public boolean containsValue() {
        if (isEmpty()) { return false; }
        return head.isValue() || tail.containsValue();
    }

    public ParseValue getLowestOffsetValue() {
        return ByOffset.getLowestOffsetValue(this);
    }

    public boolean hasGraphAtRef(final long ref) {
        return ByOffset.hasGraphAtRef(this, ref);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public ParseGraph reverse() {
        return Reversal.reverse(this, EMPTY);
    }

    public ParseValue get(final String name) {
        return ByName.get(this, name);
    }

    public ParseItem get(final Token definition) {
        return ByToken.get(this, definition);
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

    public ParseValueList getAll(final String name) {
        return ByName.getAll(this, name);
    }

    public ParseItemList getAll(final Token definition) {
        return ByToken.getAll(this, definition);
    }

    /**
     * @param lastHead The first item (bottom-up) to be excluded
     * @return The subgraph of this graph starting past (bottom-up) the provided lastHead
     */
    public ParseGraph getGraphAfter(final ParseItem lastHead) {
        return ByItem.getGraphAfter(this, lastHead);
    }

    @Override
    public long getSequenceId() {
        return sequenceId;
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
