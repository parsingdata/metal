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
        return eval(vl, env, enc, OptionalValueList.EMPTY);
    }

    private OptionalValueList eval(final OptionalValueList vl, final Environment env, final Encoding enc, final OptionalValueList out) {
        if (vl.isEmpty()) { return out; }
        return eval(vl.tail, env, enc, out).add(flip(vl.head, enc));
    }

    private OptionalValue flip(final OptionalValue op, final Encoding enc) {
        if (!op.isPresent()) { return op; }
        final BitSet value = op.get().asBitSet();
        value.flip(0, op.get().getValue().length * 8);
        return OptionalValue.of(ConstantFactory.createFromBitSet(value, op.get().getValue().length, enc));
    }

}
