/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.token.Token;

public class ParseGraph extends ImmutableObject implements ParseItem {

    public final ParseItem head;
    public final ParseGraph tail;
    public final boolean branched;
    public final Token definition;
    public final long size;

    public static final Token NONE = new Token("NONE", null) {
        @Override protected Optional<ParseState> parseImpl(final Environment environment) { throw new IllegalStateException("This placeholder may not be invoked."); }
        @Override public String toString() { return "None"; }
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
        this.tail = checkNotNull(tail, "tail");
        this.branched = branched;
        this.definition = checkNotNull(definition, "definition");
        size = tail.size + 1;
    }

    private ParseGraph(final ParseItem head, final ParseGraph tail, final Token definition) {
        this(head, tail, definition, false);
    }

    protected ParseGraph add(final ParseValue head) {
        if (branched) {
            return new ParseGraph(this.head.asGraph().add(head), tail, definition, true);
        }
        return new ParseGraph(head, this, definition);
    }

    protected ParseGraph add(final ParseReference parseReference) {
        if (branched) {
            return new ParseGraph(head.asGraph().add(parseReference), tail, definition, true);
        }
        return new ParseGraph(parseReference, this, definition);
    }

    protected ParseGraph addBranch(final Token definition) {
        if (branched) {
            return new ParseGraph(head.asGraph().addBranch(definition), tail, this.definition, true);
        }
        return new ParseGraph(new ParseGraph(definition), this, this.definition, true);
    }

    protected ParseGraph closeBranch() {
        if (!branched) {
            throw new IllegalStateException("Cannot close branch that is not open.");
        }
        if (head.asGraph().branched) {
            return new ParseGraph(head.asGraph().closeBranch(), tail, definition, true);
        }
        return new ParseGraph(head, tail, definition, false);
    }

    public boolean isEmpty() { return size == 0; }

    /**
     * @return The first value (bottom-up) in this graph
     */
    public Optional<ParseValue> current() {
        return current(ImmutableList.create(this)).computeResult();
    }

    private Trampoline<Optional<ParseValue>> current(final ImmutableList<ParseItem> items) {
        if (items.isEmpty()) {
            return complete(Optional::empty);
        }
        final ParseItem item = items.head();
        if (item.isValue()) {
            return complete(() -> Optional.of(item.asValue()));
        }
        if (item.isGraph() && !item.asGraph().isEmpty()) {
            return intermediate(() -> current(items.tail().addHead(item.asGraph().tail)
                                                        .addHead(item.asGraph().head)));
        }
        return intermediate(() -> current(items.tail()));
    }

    @Override public boolean isGraph() { return true; }
    @Override public ParseGraph asGraph() { return this; }
    @Override public Token getDefinition() { return definition; }

    @Override
    public String toString() {
        if (this.equals(EMPTY)) {
            return "pg(EMPTY)";
        }
        if (isEmpty()) {
            return "pg(terminator:" + definition.getClass().getSimpleName() + ")";
        }
        return "pg(" + head + "," + tail + "," + branched + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(head, ((ParseGraph)obj).head)
            && Objects.equals(tail, ((ParseGraph)obj).tail)
            && Objects.equals(branched, ((ParseGraph)obj).branched)
            && Objects.equals(definition, ((ParseGraph)obj).definition);
            // The size field is excluded from equals() and hashCode() because it is cached data.
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), head, tail, branched, definition);
    }

}
