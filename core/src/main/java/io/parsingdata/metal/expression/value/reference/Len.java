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

package io.parsingdata.metal.expression.value.reference;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Len extends UnaryValueExpression {

    public Len(final ValueExpression op) {
        super(op);
    }

    @Override
    public OptionalValue eval(final Value v, final Environment env, final Encoding enc) {
        return OptionalValue.of(num(v.getValue().length));
    }

    private static Value num(final long length) {
        return ConstantFactory.createFromNumeric(length, new Encoding(Sign.SIGNED));
    }
}
