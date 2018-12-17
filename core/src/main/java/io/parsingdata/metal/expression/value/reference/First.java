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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ValueExpression} that represents the first {@link Value} returned
 * by evaluating its <code>operands</code> field.
 */
public class First implements ValueExpression {

    public final ValueExpression operands;

    public First(final ValueExpression operands) {
        this.operands = checkNotNull(operands, "operands");
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseState parseState, final Encoding encoding) {
        final ImmutableList<Optional<Value>> evaluatedOperands = operands.eval(parseState, encoding);
        return evaluatedOperands.isEmpty() ? evaluatedOperands : ImmutableList.create(getFirst(evaluatedOperands).computeResult());
    }

    private Trampoline<Optional<Value>> getFirst(final ImmutableList<Optional<Value>> operandsValues) {
        return operandsValues.tail.isEmpty() ? complete(() -> operandsValues.head) : intermediate(() -> getFirst(operandsValues.tail));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operands + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(operands, ((First)obj).operands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), operands);
    }

}
