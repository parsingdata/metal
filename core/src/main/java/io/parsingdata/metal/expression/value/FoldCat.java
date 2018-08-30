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

import static io.parsingdata.metal.data.Slice.createFromSource;
import static java.math.BigInteger.ZERO;

import java.util.Optional;

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
public class FoldCat extends OneToOneValueExpression {

    public FoldCat(final ValueExpression operand) {
        super(operand);
    }

    @Override
    public Optional<Value> eval(final ImmutableList<Optional<Value>> list, final ParseState parseState, final Encoding encoding) {
        return ConcatenatedValueSource.create(list)
                .flatMap(source -> createFromSource(source, ZERO, source.length))
                .map(slice -> new Value(slice, encoding));
    }

}
