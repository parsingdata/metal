/*
 * Copyright 2013-2023 Netherlands Forensic Institute
 * Copyright 2021-2023 Infix Technologies B.V.
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

import static io.parsingdata.metal.Util.checkContainsNoNulls;

import java.util.Arrays;
import java.util.Objects;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that joins multiple {@link ValueExpression}s by concatenating
 * the individual results to a single list.
 * <p>
 * A Join expression can have zero or more expressions. If none is provided, this will return an empty list.
 * Else, each expression is evaluated and concatenated to a single list.
 */
public class Join extends ImmutableObject implements ValueExpression {

    private final ValueExpression[] expressions;

    public Join(final ValueExpression... expressions) {
        this.expressions = checkContainsNoNulls(expressions, "expression");
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        return Arrays.stream(expressions)
            .map(e -> {
                return e.eval(parseState, encoding);
            })
            .reduce(new ImmutableList<>(), ImmutableList::addList, ImmutableList::addList);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + Arrays.toString(expressions) + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Arrays.equals(expressions, ((Join)obj).expressions);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), Arrays.hashCode(expressions));
    }
}
