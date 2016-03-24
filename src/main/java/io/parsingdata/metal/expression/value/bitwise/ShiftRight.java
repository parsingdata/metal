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

package io.parsingdata.metal.expression.value.bitwise;

import java.util.BitSet;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class ShiftRight extends BinaryValueExpression {

    public ShiftRight(final ValueExpression lop, final ValueExpression rop) {
        super(lop, rop);
    }

    @Override
    public OptionalValue eval(final Value lv, final Value rv, final Environment env, final Encoding enc) {
        final BitSet lbs = lv.asBitSet();
        final int shift = rv.asNumeric().intValue();
        return OptionalValue.of(ConstantFactory.createFromBitSet(lbs.get(shift, Math.max(shift, lbs.length())), lv.getValue().length, enc));
    }

}
