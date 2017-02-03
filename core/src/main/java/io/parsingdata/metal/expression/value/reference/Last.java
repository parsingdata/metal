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

import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ValueExpression} that represents the last {@link Value} returned
 * by evaluating its <code>operand</code>.
 */
public class Last implements ValueExpression {

    public final ValueExpression operand;

    public Last(final ValueExpression operand) {
        this.operand = checkNotNull(operand, "operand");
    }

    @Override
    public ImmutableList<Optional<Value>> eval(final ParseGraph graph, final Encoding encoding) {
        final ImmutableList<Optional<Value>> list = operand.eval(graph, encoding);
        return list.isEmpty() ? list : ImmutableList.create(list.head);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + operand + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
            && getClass() == obj.getClass()
            && Objects.equals(operand, ((Last)obj).operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

}
