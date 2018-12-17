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

package io.parsingdata.metal.token;

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotEmpty;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.success;

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
 * If the <code>ValueExpressions</code> evaluate to lists, they are treated
 * as sets of values to attempt. If <code>stepSize</code> is negative,
 * <code>maxSize</code> must be smaller than <code>initialSize</code>.
 * Parsing fails if <code>stepSize</code> is zero.
 *
 * @see ValueExpression
 */
public class Until extends Token {

    public static final ValueExpression DEFAULT_INITIAL = con(0);
    public static final ValueExpression DEFAULT_STEP = con(1);
    public static final ValueExpression DEFAULT_MAX = con(Integer.MAX_VALUE);

    public final ValueExpression initialSizes;
    public final ValueExpression stepSizes;
    public final ValueExpression maxSizes;
    public final Token terminator;

    public Until(final String name, final ValueExpression initialSizes, final ValueExpression stepSizes, final ValueExpression maxSizes, final Token terminator, final Encoding encoding) {
        super(checkNotEmpty(name, "name"), encoding);
        this.initialSizes = initialSizes == null ? DEFAULT_INITIAL : initialSizes;
        this.stepSizes = stepSizes == null ? DEFAULT_STEP : stepSizes;
        this.maxSizes = maxSizes == null ? DEFAULT_MAX : maxSizes;
        this.terminator = checkNotNull(terminator, "terminator");
    }

    @Override
    protected Optional<ParseState> parseImpl(final Environment environment) {
        return handleInterval(environment, initialSizes.eval(environment.parseState, environment.encoding), stepSizes.eval(environment.parseState, environment.encoding), maxSizes.eval(environment.parseState, environment.encoding)).computeResult();
    }

    private Trampoline<Optional<ParseState>> handleInterval(final Environment environment, final ImmutableList<Optional<Value>> initialSizeValues, final ImmutableList<Optional<Value>> stepSizeValues, final ImmutableList<Optional<Value>> maxSizeValues) {
        if (checkNotValidList(initialSizeValues) || checkNotValidList(stepSizeValues) || checkNotValidList(maxSizeValues)) {
            return complete(Util::failure);
        }
        return iterate(environment, getNumeric(initialSizeValues), getNumeric(stepSizeValues), getNumeric(maxSizeValues))
            .computeResult()
            .map(nextParseState -> complete(() -> success(nextParseState)))
            .orElseGet(() -> intermediate(() -> handleInterval(environment, initialSizeValues.tail, stepSizeValues.tail, maxSizeValues.tail)));
    }

    private Trampoline<Optional<ParseState>> iterate(final Environment environment, final BigInteger currentSizeValue, final BigInteger stepSizeValue, final BigInteger maxSizeValue) {
        if (stepSizeValue.compareTo(ZERO) == 0 ||
            (stepSizeValue.compareTo(ZERO) > 0 && currentSizeValue.compareTo(maxSizeValue) > 0) ||
            (stepSizeValue.compareTo(ZERO) < 0 && currentSizeValue.compareTo(maxSizeValue) < 0)) {
            return complete(Util::failure);
        }
        return environment.parseState
            .slice(currentSizeValue)
            .map(slice -> parseSlice(environment, currentSizeValue, stepSizeValue, maxSizeValue, slice))
            .orElseGet(() -> complete(Util::failure));
    }

    private Trampoline<Optional<ParseState>> parseSlice(final Environment environment, final BigInteger currentSizeValue, final BigInteger stepSizeValue, final BigInteger maxSizeValue, final Slice slice) {
        return (currentSizeValue.compareTo(ZERO) == 0 ? Optional.of(environment.parseState) : environment.parseState.add(new ParseValue(name, this, slice, environment.encoding)).seek(environment.parseState.offset.add(currentSizeValue)))
            .map(preparedParseState -> terminator.parse(environment.withParseState(preparedParseState)))
            .orElseGet(Util::failure)
            .map(parsedParseState -> complete(() -> success(parsedParseState)))
            .orElseGet(() -> intermediate(() -> iterate(environment, currentSizeValue.add(stepSizeValue), stepSizeValue, maxSizeValue)));
    }

    private boolean checkNotValidList(final ImmutableList<Optional<Value>> list) {
        return list.isEmpty() || !list.head.isPresent();
    }

    private BigInteger getNumeric(final ImmutableList<Optional<Value>> list) {
        return list.head.get().asNumeric();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + initialSizes + "," + stepSizes + "," + maxSizes + "," + terminator + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(initialSizes, ((Until)obj).initialSizes)
            && Objects.equals(stepSizes, ((Until)obj).stepSizes)
            && Objects.equals(maxSizes, ((Until)obj).maxSizes)
            && Objects.equals(terminator, ((Until)obj).terminator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), initialSizes, stepSizes, maxSizes, terminator);
    }

}
