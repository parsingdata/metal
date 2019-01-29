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
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that expands a result by copying and concatenating
 * it a specified amount of times.
 * <p>
 * An Expand expression has two operands: <code>bases</code> and
 * <code>count</code> (both {@link ValueExpression}s). Both operands are
 * evaluated. An <code>IllegalStateException</code> is thrown if evaluating
 * <code>count</code> yields more than a single value. Multiple copies of the
 * result of evaluating <code>bases</code> are concatenated. The amount of copies
 * equals the result of evaluating <code>count</code>.
 */
public class Expand implements ValueExpression {

    public final ValueExpression bases;
    public final ValueExpression count;

    public Expand(final ValueExpression bases, final ValueExpression count) {
        this.bases = checkNotNull(bases, "bases");
        this.count = checkNotNull(count, "count");
    }

    @Override
    public Optional<ImmutableList<Value>> eval(final ParseState parseState, final Encoding encoding) {
        final Optional<ImmutableList<Value>> baseList = bases.eval(parseState, encoding);
        if (!baseList.isPresent() || baseList.get().isEmpty()) {
            return Optional.empty();
        }
        final Optional<ImmutableList<Value>> countList = count.eval(parseState, encoding);
        if (!countList.isPresent() || countList.get().size != 1) {
            throw new IllegalArgumentException("Count must evaluate to a single non-empty value.");
        }
        return Optional.of(expand(baseList.get(), countList.get().head.asNumeric().intValueExact(), new ImmutableList<>()).computeResult());
    }

    private Trampoline<ImmutableList<Value>> expand(final ImmutableList<Value> baseList, final int countValue, final ImmutableList<Value> aggregate) {
        if (countValue < 1) {
            return complete(() -> aggregate);
        }
        return intermediate(() -> expand(baseList, countValue - 1, aggregate.add(baseList)));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + bases + "," + count + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(bases, ((Expand)obj).bases)
            && Objects.equals(count, ((Expand)obj).count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), bases, count);
    }

}
