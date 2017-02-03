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

import static io.parsingdata.metal.expression.value.ConstantFactory.makeConstantSlice;

import java.util.Optional;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link BinaryValueExpression} that concatenates values at the byte-level.
 */
public class Cat extends BinaryValueExpression {

    public Cat(final ValueExpression left, final ValueExpression right) {
        super(left, right);
    }

    @Override
    public Optional<Value> eval(final Value left, final Value right, final ParseGraph graph, final Encoding encoding) {
        final byte[] leftBytes = left.getValue();
        final byte[] rightBytes = right.getValue();
        final byte[] concatenatedBytes = new byte[leftBytes.length + rightBytes.length];
        System.arraycopy(leftBytes, 0, concatenatedBytes, 0, leftBytes.length);
        System.arraycopy(rightBytes, 0, concatenatedBytes, leftBytes.length, rightBytes.length);
        return Optional.of(new Value(makeConstantSlice(concatenatedBytes), encoding));
    }

}
