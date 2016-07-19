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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;

import static io.parsingdata.metal.Util.checkNotNull;

public abstract class UnaryValueExpression implements ValueExpression {

    public final ValueExpression operand;

    public UnaryValueExpression(final ValueExpression operand) {
        this.operand = checkNotNull(operand, "operand");
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        return eval(operand.eval(env, enc), env, enc);
    }

    private OptionalValueList eval(final OptionalValueList vl, final Environment env, final Encoding enc) {
        if (vl.isEmpty()) { return vl; }
        return eval(vl.tail, env, enc).add(vl.head.isPresent() ? eval(vl.head.get(), env, enc) : vl.head);
    }

    public abstract OptionalValue eval(final Value value, final Environment env, final Encoding enc);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operand + ")";
    }

}
