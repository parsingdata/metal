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

package io.parsingdata.metal.expression;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Interface for all Expression implementations.
 * <p>
 * An Expression is evaluated by calling the
 * {@link #eval(ParseState, Encoding)} method. Given a {@link ParseState} and
 * an {@link Encoding}, the evaluation either succeeds or fails. Main use of
 * expressions is to define predicates that are evaluated during parsing.
 *
 * @see io.parsingdata.metal.token.Def
 * @see io.parsingdata.metal.token.Pre
 * @see io.parsingdata.metal.token.While
 */
@FunctionalInterface
public interface Expression {

    boolean eval(ParseState parseState, Encoding encoding);

}
