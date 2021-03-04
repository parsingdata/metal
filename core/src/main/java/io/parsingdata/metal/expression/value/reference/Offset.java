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

package io.parsingdata.metal.expression.value.reference;

import java.util.Optional;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ValueExpression} that represents the offset of the {@link Value}s
 * returned by evaluating its <code>operand</code>.
 * <p>
 * Only {@link ParseValue}s have an offset, since they originate in the input.
 * If a result does not have an offset (such as the {@link Value}s returned by
 * {@link io.parsingdata.metal.expression.value.Const}), empty is returned.
 */
public class Offset extends UnaryValueExpression {

    public Offset(final ValueExpression operand) { super(operand); }

    @Override
    public Optional<Value> eval(final Value value, final ParseState parseState, final Encoding encoding) {
        return Optional.of(ConstantFactory.createFromNumeric(value.slice().offset, value.encoding()));
    }

}
