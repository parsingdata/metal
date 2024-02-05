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

package io.parsingdata.metal.expression.value;

import static java.math.BigInteger.ZERO;

import static io.parsingdata.metal.data.Slice.createFromSource;

import java.util.Optional;

import io.parsingdata.metal.data.ConcatenatedValueSource;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link BinaryValueExpression} that concatenates values at the byte-level.
 */
public class Cat extends BinaryValueExpression {

    public Cat(final ValueExpression left, final ValueExpression right) {
        super(left, right);
    }

    @Override
    public Optional<Value> eval(final Value leftValue, final Value rightValue, final ParseState parseState, final Encoding encoding) {
        return ConcatenatedValueSource.create(ImmutableList.create(leftValue).addHead(rightValue))
            .flatMap(source -> createFromSource(source, ZERO, leftValue.length().add(rightValue.length())))
            .map(source -> new CoreValue(source, encoding));
    }

}
