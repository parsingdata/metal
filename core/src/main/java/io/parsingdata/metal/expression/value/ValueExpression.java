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

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Interface for all ValueExpression implementations.
 * <p>
 * A ValueExpression is an expression that is evaluated by executing its
 * {@link #eval(ParseState, Encoding)} method. It yields a list of
 * {@link Value} objects.
 * <p>
 * As context, it receives the current {@link ParseState} object as
 * well as the current {@link Encoding} object.
 */
@FunctionalInterface
public interface ValueExpression {

    ImmutableList<Value> eval(ParseState parseState, Encoding encoding);

}
