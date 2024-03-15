/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.data.Slice.createFromSource;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ConcatenatedValueSource;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link SingleValueExpression} that represents an optimized version of a
 * {@link FoldLeft} operation with a {@link Cat} ValueExpression as reducer.
 *
 * @see FoldLeft
 * @see Cat
 */
public class FoldCat extends ImmutableObject implements SingleValueExpression {

    public final ValueExpression operand;

    public FoldCat(final ValueExpression operand) {
        this.operand = operand;
    }

    @Override
    public Optional<Value> evalSingle(final ParseState parseState, final Encoding encoding) {
        return ConcatenatedValueSource.create(operand.eval(parseState, encoding))
            .flatMap(source -> createFromSource(source, ZERO, source.length))
            .map(slice -> new CoreValue(slice, encoding));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operand + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(operand, ((FoldCat)obj).operand);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), operand);
    }

}
