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
 * <code>Environment</code> and an attempt is made to parse the
 * <code>terminator</code>. If it succeeds, the resulting environment is
 * returned. Otherwise, <code>stepSize</code> is added to the
 * <code>initialSize</code> and if the resulting value is below
 * <code>maxSize</code>, a value with the resulting size is added to the
 * original <code>Environment</code> and a new attempt to parse the
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
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) {
        return handleInterval(scope, environment, initialSize.eval(environment.order, encoding), stepSize.eval(environment.order, encoding), maxSize.eval(environment.order, encoding), encoding).computeResult();
    }

    private Trampoline<Optional<Environment>> handleInterval(final String scope, final Environment environment, final ImmutableList<Optional<Value>> initialSizes, final ImmutableList<Optional<Value>> stepSizes, final ImmutableList<Optional<Value>> maxSizes, final Encoding encoding) {
        if (checkNotValidList(initialSizes) || checkNotValidList(stepSizes) || checkNotValidList(maxSizes)) { return complete(Util::failure); }
        return iterate(scope, environment, getNumeric(initialSizes), getNumeric(stepSizes), getNumeric(maxSizes), encoding).computeResult()
            .map(nextEnvironment -> complete(() -> success(nextEnvironment)))
            .orElseGet(() -> intermediate(() -> handleInterval(scope, environment, initialSizes.tail, stepSizes.tail, maxSizes.tail, encoding)));
    }

    private Trampoline<Optional<Environment>> iterate(final String scope, final Environment environment, final BigInteger currentSize, final BigInteger stepSize, final BigInteger maxSize, final Encoding encoding) {
        if (stepSize.compareTo(ZERO) == 0 ||
            stepSize.compareTo(ZERO) > 0 && currentSize.compareTo(maxSize) > 0 ||
            stepSize.compareTo(ZERO) < 0 && currentSize.compareTo(maxSize) < 0) { return complete(Util::failure); }
        return environment
            .slice(currentSize)
            .map(slice -> parseSlice(scope, environment, currentSize, stepSize, maxSize, encoding, slice))
            .orElse(complete(Util::failure));
    }

    private Trampoline<Optional<Environment>> parseSlice(String scope, Environment environment, BigInteger currentSize, BigInteger stepSize, BigInteger maxSize, Encoding encoding, Slice slice) {
        return terminator
            .parse(scope, currentSize.compareTo(ZERO) == 0 ? environment : environment.add(new ParseValue(name, this, slice, encoding)).seek(environment.offset.add(currentSize)), encoding)
            .map(nextEnvironment -> complete(() -> success(nextEnvironment)))
            .orElseGet(() -> intermediate(() -> iterate(scope, environment, currentSize.add(stepSize), stepSize, maxSize, encoding)));
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
