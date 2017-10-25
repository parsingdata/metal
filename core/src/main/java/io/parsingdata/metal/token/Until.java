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
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.data.callback.Callbacks;
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

    public final ValueExpression initialSize;
    public final ValueExpression stepSize;
    public final ValueExpression maxSize;
    public final Token terminator;

    public Until(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final ValueExpression maxSize, final Token terminator, final Encoding encoding) {
        super(checkNotEmpty(name, "name"), encoding);
        this.initialSize = initialSize == null ? DEFAULT_INITIAL : initialSize;
        this.stepSize = stepSize == null ? DEFAULT_STEP : stepSize;
        this.maxSize = maxSize == null ? DEFAULT_MAX : maxSize;
        this.terminator = checkNotNull(terminator, "terminator");
    }

    @Override
    protected Optional<ParseState> parseImpl(final String scope, final ParseState parseState, final Callbacks callbacks, final Encoding encoding) {
        return handleInterval(scope, parseState, initialSize.eval(parseState.order, encoding), stepSize.eval(parseState.order, encoding), maxSize.eval(parseState.order, encoding), callbacks, encoding).computeResult();
    }

    private Trampoline<Optional<ParseState>> handleInterval(final String scope, final ParseState parseState, final ImmutableList<Optional<Value>> initialSizes, final ImmutableList<Optional<Value>> stepSizes, final ImmutableList<Optional<Value>> maxSizes, final Callbacks callbacks, final Encoding encoding) {
        if (checkNotValidList(initialSizes) || checkNotValidList(stepSizes) || checkNotValidList(maxSizes)) {
            return complete(Util::failure);
        }
        return iterate(scope, parseState, getNumeric(initialSizes), getNumeric(stepSizes), getNumeric(maxSizes), callbacks, encoding)
            .computeResult()
            .map(nextParseState -> complete(() -> success(nextParseState)))
            .orElseGet(() -> intermediate(() -> handleInterval(scope, parseState, initialSizes.tail, stepSizes.tail, maxSizes.tail, callbacks, encoding)));
    }

    private Trampoline<Optional<ParseState>> iterate(final String scope, final ParseState parseState, final BigInteger currentSize, final BigInteger stepSize, final BigInteger maxSize, final Callbacks callbacks, final Encoding encoding) {
        if (stepSize.compareTo(ZERO) == 0 ||
            stepSize.compareTo(ZERO) > 0 && currentSize.compareTo(maxSize) > 0 ||
            stepSize.compareTo(ZERO) < 0 && currentSize.compareTo(maxSize) < 0) {
            return complete(Util::failure);
        }
        return parseState
            .slice(currentSize)
            .map(slice -> parseSlice(scope, parseState, currentSize, stepSize, maxSize, callbacks, encoding, slice))
            .orElseGet(() -> complete(Util::failure));
    }

    private Trampoline<Optional<ParseState>> parseSlice(String scope, ParseState parseState, BigInteger currentSize, BigInteger stepSize, BigInteger maxSize, final Callbacks callbacks, Encoding encoding, Slice slice) {
        return (currentSize.compareTo(ZERO) == 0 ? Optional.of(parseState) : parseState.add(new ParseValue(name, this, slice, encoding)).seek(parseState.offset.add(currentSize)))
            .map(preparedParseState -> terminator.parse(scope, preparedParseState, callbacks, encoding))
            .orElseGet(Util::failure)
            .map(parsedParseState -> complete(() -> success(parsedParseState)))
            .orElseGet(() -> intermediate(() -> iterate(scope, parseState, currentSize.add(stepSize), stepSize, maxSize, callbacks, encoding)));
    }

    private boolean checkNotValidList(ImmutableList<Optional<Value>> list) {
        return list.isEmpty() || !list.head.isPresent();
    }

    private BigInteger getNumeric(ImmutableList<Optional<Value>> list) {
        return list.head.get().asNumeric();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + initialSize + "," + stepSize + "," + maxSize + "," + terminator + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(initialSize, ((Until)obj).initialSize)
            && Objects.equals(stepSize, ((Until)obj).stepSize)
            && Objects.equals(maxSize, ((Until)obj).maxSize)
            && Objects.equals(terminator, ((Until)obj).terminator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), initialSize, stepSize, maxSize, terminator);
    }

}
