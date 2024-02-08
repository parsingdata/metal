/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import java.util.Optional;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Interface for all SingleValueExpression implementations.
 * <p>
 * A SingleValueExpression is an expression that is evaluated by executing its
 * {@link #evalSingle(ParseState, Encoding)} method. It yields an {@link Optional}
 * {@link Value} object.
 * <p>
 * As context, it receives the current <code>ParseState</code> object as
 * well as the current <code>Encoding</code> object.
 */
@SuppressWarnings("FunctionalInterfaceMethodChanged") // What we do is in line with error-prone's advice
@FunctionalInterface
public interface SingleValueExpression extends ValueExpression {

    Optional<Value> evalSingle(ParseState parseState, Encoding encoding);

    @Override
    default ImmutableList<Value> eval(ParseState parseState, Encoding encoding) {
        return evalSingle(parseState, encoding)
            .map(ImmutableList::create)
            .orElseGet(ImmutableList::new);
    }

}
