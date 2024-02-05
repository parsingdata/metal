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

package io.parsingdata.metal.token;

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotEmpty;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.success;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link Token} that specifies a value to parse in the input until
 * another token is parsed.
 * <p>
 * An Until consists of an <code>initialSize</code>, a <code>stepSize</code>
 * and a <code>maxSize</code> (all {@link ValueExpression}s) and a
 * <code>terminator</code>(a {@link Token}). First <code>initialSize</code>,
 * <code>stepSize</code>, and <code>maxSize</code> are evaluated. Using this
 * token's name, a value of length <code>initialSize</code> is added to the
 * <code>ParseState</code> and an attempt is made to parse the
 * <code>terminator</code>. If it succeeds, the resulting parseState is
 * returned. Otherwise, <code>stepSize</code> is added to the
 * <code>initialSize</code> and if the resulting value is below
 * <code>maxSize</code>, a value with the resulting size is added to the
 * original <code>ParseState</code> and a new attempt to parse the
 * <code>terminator</code> is made. Parsing fails if no combination of any size
 * is found where the <code>terminator</code> parses successfully.
 * <p>
 * Whether the resulting <code>ParseState</code> includes the parsed
 * terminator, depends on the value of the <code>includeTerminator</code>
 * argument.
 * <p>
 * If the <code>ValueExpressions</code> evaluate to lists, they are treated
 * as sets of values to attempt. If <code>stepSize</code> is negative,
 * <code>maxSize</code> must be smaller than <code>initialSize</code>.
 * Parsing fails if <code>stepSize</code> is zero.
 *
 * @see ValueExpression
 */
public class DefUntil extends Token {

    public static final ValueExpression DEFAULT_INITIAL = con(0);
    public static final ValueExpression DEFAULT_STEP = con(1);
    public static final ValueExpression DEFAULT_MAX = con(Integer.MAX_VALUE);

    public final ValueExpression initialSize;
    public final ValueExpression stepSize;
    public final ValueExpression maxSize;
    public final Token terminator;

    public DefUntil(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final ValueExpression maxSize, final Token terminator, final Encoding encoding) {
        super(checkNotEmpty(name, "name"), encoding);
        this.initialSize = initialSize == null ? DEFAULT_INITIAL : initialSize;
        this.stepSize = stepSize == null ? DEFAULT_STEP : stepSize;
        this.maxSize = maxSize == null ? DEFAULT_MAX : maxSize;
        this.terminator = checkNotNull(terminator, "terminator");
    }

    @Override
    protected Optional<ParseState> parseImpl(final Environment environment) {
        return handleInterval(environment, initialSize.eval(environment.parseState, environment.encoding), stepSize.eval(environment.parseState, environment.encoding), maxSize.eval(environment.parseState, environment.encoding)).computeResult();
    }

    private Trampoline<Optional<ParseState>> handleInterval(final Environment environment, final ImmutableList<Value> initialSizes, final ImmutableList<Value> stepSizes, final ImmutableList<Value> maxSizes) {
        if (checkNotValidList(initialSizes) || checkNotValidList(stepSizes) || checkNotValidList(maxSizes)) {
            return complete(Util::failure);
        }
        return iterate(environment, getNumeric(initialSizes), getNumeric(stepSizes), getNumeric(maxSizes))
            .computeResult()
            .map(nextParseState -> complete(() -> success(nextParseState)))
            .orElseGet(() -> intermediate(() -> handleInterval(environment, initialSizes.tail(), stepSizes.tail(), maxSizes.tail())));
    }

    private Trampoline<Optional<ParseState>> iterate(final Environment environment, final BigInteger currentSize, final BigInteger stepSize, final BigInteger maxSize) {
        if (stepSize.compareTo(ZERO) == 0 ||
            (stepSize.compareTo(ZERO) > 0 && currentSize.compareTo(maxSize) > 0) ||
            (stepSize.compareTo(ZERO) < 0 && currentSize.compareTo(maxSize) < 0)) {
            return complete(Util::failure);
        }
        return environment.parseState
            .slice(currentSize)
            .map(slice -> parseSlice(environment, currentSize, stepSize, maxSize, slice))
            .orElseGet(() -> complete(Util::failure));
    }

    private Trampoline<Optional<ParseState>> parseSlice(final Environment environment, final BigInteger currentSize, final BigInteger stepSize, final BigInteger maxSize, final Slice slice) {
        return (currentSize.compareTo(ZERO) == 0 ? Optional.of(environment.parseState) : environment.parseState.add(new ParseValue(environment.scope, this, slice, environment.encoding)).seek(environment.parseState.offset.add(currentSize)))
            .map(preparedParseState -> terminator.parse(environment.withParseState(preparedParseState)).map(ignore -> preparedParseState))
            .orElseGet(Util::failure)
            .map(parseState -> complete(() -> success(parseState)))
            .orElseGet(() -> intermediate(() -> iterate(environment, currentSize.add(stepSize), stepSize, maxSize)));
    }

    private boolean checkNotValidList(final ImmutableList<Value> list) {
        return list.isEmpty() || list.head().equals(NOT_A_VALUE);
    }

    private BigInteger getNumeric(final ImmutableList<Value> list) {
        return list.head().asNumeric();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + initialSize + "," + stepSize + "," + maxSize + "," + terminator + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(initialSize, ((DefUntil)obj).initialSize)
            && Objects.equals(stepSize, ((DefUntil)obj).stepSize)
            && Objects.equals(maxSize, ((DefUntil)obj).maxSize)
            && Objects.equals(terminator, ((DefUntil)obj).terminator);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(super.immutableHashCode(), initialSize, stepSize, maxSize, terminator);
    }

}
