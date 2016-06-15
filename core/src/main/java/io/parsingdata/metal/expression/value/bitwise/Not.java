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
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.*;

import java.util.BitSet;

public class Not extends UnaryValueExpression {

    public Not(final ValueExpression op) {
        super(op);
    }

    @Override
    public OptionalValueList eval(final OptionalValueList vl, final Environment env, final Encoding enc) {
        if (!vl.tail.isEmpty()) { return eval(vl.tail, env, enc); }
        if (!vl.head.isPresent()) {
            return
        } else {
            final BitSet value = vl.head.get().asBitSet();
            value.flip(0, vl.head.get().getValue().length * 8);
            return OptionalValue.of(ConstantFactory.createFromBitSet(value, op.getValue().length, enc));
        }
    }

}
