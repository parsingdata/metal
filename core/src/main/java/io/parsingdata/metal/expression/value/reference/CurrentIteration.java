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

package io.parsingdata.metal.expression.value.reference;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;
import static io.parsingdata.metal.expression.value.ConstantFactory.createFromNumeric;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ImmutablePair;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.SingleValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.While;

/**
 * A {@link SingleValueExpression} that represents the 0-based current iteration in an
 * iterable {@link Token} (when {@link Token#isIterable()} returns true, e.g. when
 * inside a {@link Rep}, {@link RepN}) or {@link While}).
 *
 * The <code>level</code> (a {@link SingleValueExpression} operand can be used
 * to specify the relative nesting level of the iteration count that is required.
 * <code>0</code> denotes the current (deepest) nesting level, <code>1</code> the
 * level above it, and so on.
 */
public class CurrentIteration extends ImmutableObject implements SingleValueExpression {

    private final SingleValueExpression level;

    public CurrentIteration(final SingleValueExpression level) {
        this.level = checkNotNull(level, "level");
    }

    @Override
    public Optional<Value> evalSingle(final ParseState parseState, final Encoding encoding) {
        final Optional<Value> levelValue = level.evalSingle(parseState, encoding);
        if (levelValue.isEmpty() || levelValue.get().equals(NOT_A_VALUE) || levelValue.get().asNumeric().compareTo(ZERO) < 0) {
            return Optional.of(NOT_A_VALUE);
        }
        if ((long) parseState.iterations.size() <= levelValue.get().asNumeric().longValueExact()) {
            return Optional.empty();
        }
        return getIterationRecursive(parseState.iterations, levelValue.get().asNumeric()).computeResult();
    }

    private Trampoline<Optional<Value>> getIterationRecursive(final ImmutableList<ImmutablePair<Token, BigInteger>> iterations, final BigInteger levelValue) {
        if (levelValue.compareTo(ZERO) == 0) {
            return complete(() -> Optional.of(createFromNumeric(iterations.head().right, DEFAULT_ENCODING)));
        }
        return intermediate(() -> getIterationRecursive(iterations.tail(), levelValue.subtract(ONE)));
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
    public int immutableHashCode() {
        return Objects.hash(getClass(), level);
    }

}
