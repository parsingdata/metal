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

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.data.Slice.createFromSource;

import java.util.Objects;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ConcatenatedValueSource;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that represents an optimized version of a
 * {@link FoldLeft} operation with a {@link Cat} ValueExpression as reducer.
 *
 * @see FoldLeft
 * @see Cat
 */
public class FoldCat implements ValueExpression {

    public final ValueExpression operand;

    public FoldCat(final ValueExpression operand) {
        this.operand = operand;
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        return ConcatenatedValueSource.create(operand.eval(parseState, encoding))
            .flatMap(source -> createFromSource(source, ZERO, source.length))
            .map(slice -> ImmutableList.create(new Value(slice, encoding)))
            .orElseGet(ImmutableList::new);
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
    public int hashCode() {
        return Objects.hash(getClass(), operand);
    }

}
