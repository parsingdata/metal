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

package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that expands a result by copying and concatenating
 * it a specified amount of times.
 * <p>
 * An Expand expression has two operands: <code>base</code> and
 * <code>count</code> (both {@link ValueExpression}s). Both operands are
 * evaluated. An <code>IllegalStateException</code> is thrown if evaluating
 * <code>count</code> yields more than a single value. Multiple copies of the
 * result of evaluating <code>base</code> are concatenated. The amount of copies
 * equals the result of evaluating <code>count</code>.
 */
public class Expand implements ValueExpression {

    public final ValueExpression base;
    public final ValueExpression count;

    public Expand(final ValueExpression base, final ValueExpression count) {
        this.base = checkNotNull(base, "base");
        this.count = checkNotNull(count, "count");
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        final ImmutableList<Optional<Value>> base = this.base.eval(parseState, encoding);
        if (base.isEmpty()) {
            return base;
        }
        final ImmutableList<Optional<Value>> count = this.count.eval(parseState, encoding);
        if (count.size != 1 || !count.head.isPresent()) {
            throw new IllegalArgumentException("Count must evaluate to a single non-empty value.");
        }
        return expand(base, count.head.get().asNumeric().intValueExact(), new ImmutableList<>()).computeResult();
    }

    private Trampoline<ImmutableList<Optional<Value>>> expand(final ImmutableList<Optional<Value>> base, final int count, final ImmutableList<Optional<Value>> aggregate) {
        if (count < 1) {
            return complete(() -> aggregate);
        }
        return intermediate(() -> expand(base, count - 1, aggregate.add(base)));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + base + "," + count + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(base, ((Expand)obj).base)
            && Objects.equals(count, ((Expand)obj).count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), base, count);
    }

}
