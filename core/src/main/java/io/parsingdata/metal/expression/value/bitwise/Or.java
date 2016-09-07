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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.*;

import java.util.BitSet;

public class Or extends BinaryValueExpression {

    public Or(final ValueExpression left, final ValueExpression right) {
        super(left, right);
    }

    @Override
    public OptionalValue eval(final Value left, final Value right, final Environment env, final Encoding enc) {
        final BitSet lbs = left.asBitSet();
        lbs.or(right.asBitSet());
        final int minSize = Math.max(left.getValue().length, right.getValue().length);
        return OptionalValue.of(ConstantFactory.createFromBitSet(lbs, minSize, enc));
    }

}
