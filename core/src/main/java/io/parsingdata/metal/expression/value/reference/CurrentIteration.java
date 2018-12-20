/*
 * Copyright 2013-2018 Netherlands Forensic Institute
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

package io.parsingdata.metal.expression.value.reference;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;
import static io.parsingdata.metal.expression.value.ConstantFactory.createFromNumeric;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ImmutablePair;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.While;

/**
 * A {@link ValueExpression} that represents the zero-based current iteration in an
 * iterable {@link Token} (when {@link Token#isIterable()} returns true, e.g. when
 * inside a {@link Rep}, {@link RepN}) or {@link While}).
 *
 * The <code>level</code> field must evaluate to a single value that represents the
 * relative nesting level from the current parsing position in the {@link ParseState}.
 */
public class CurrentIteration implements ValueExpression {

    private final ValueExpression level;

    public CurrentIteration(final ValueExpression level) {
        this.level = checkNotNull(level, "level");
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        return ImmutableList.create(getIteration(parseState, encoding));
    }

    private Optional<Value> getIteration(final ParseState parseState, final Encoding encoding) {
        final BigInteger levelValue = getLevel(parseState, encoding);
        if (parseState.iterations.size <= levelValue.longValue()) {
            return Optional.empty();
        }
        return getIterationRecursive(parseState.iterations, levelValue).computeResult();
    }

    private BigInteger getLevel(final ParseState parseState, final Encoding encoding) {
        final ImmutableList<Optional<Value>> evaluatedLevel = level.eval(parseState, encoding);
        if (evaluatedLevel.size != 1 || !evaluatedLevel.head.isPresent()) {
            throw new IllegalArgumentException("Level must evaluate to a single non-empty value.");
        }
        return evaluatedLevel.head.get().asNumeric();
    }

    private Trampoline<Optional<Value>> getIterationRecursive(final ImmutableList<ImmutablePair<Token, BigInteger>> iterations, final BigInteger levelValue) {
        if (levelValue.compareTo(ZERO) == 0) {
            return complete(() -> Optional.of(createFromNumeric(iterations.head.right, DEFAULT_ENCODING)));
        }
        return intermediate(() -> getIterationRecursive(iterations.tail, levelValue.subtract(ONE)));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
                && Objects.equals(level, ((CurrentIteration)obj).level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), level);
    }

}
