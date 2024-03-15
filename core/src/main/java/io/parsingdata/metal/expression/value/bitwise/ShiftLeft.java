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

package io.parsingdata.metal.expression.value.bitwise;

import java.util.BitSet;
import java.util.Optional;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link BinaryValueExpression} that implements the bitwise left shift
 * operation.
 */
public class ShiftLeft extends BinaryValueExpression {

    public ShiftLeft(final ValueExpression operand, final ValueExpression positions) {
        super(operand, positions);
    }

    @Override
    public Optional<Value> eval(final Value leftValue, final Value rightValue, final ParseState parseState, final Encoding encoding) {
        final BitSet leftBits = leftValue.asBitSet();
        final int shiftLeft = rightValue.asNumeric().intValueExact();
        final int bitCount = leftBits.length() + shiftLeft;
        final BitSet out = new BitSet(bitCount);
        for (int i = leftBits.nextSetBit(0); i >= 0; i = leftBits.nextSetBit(i+1)) {
            out.set(i + shiftLeft);
        }
        final int minSize = (bitCount + 7) / 8;
        return Optional.of(ConstantFactory.createFromBitSet(out, minSize, encoding));
    }

}
